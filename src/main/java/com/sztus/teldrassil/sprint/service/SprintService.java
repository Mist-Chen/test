package com.sztus.teldrassil.sprint.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sztus.framework.component.core.constant.GlobalConst;
import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.framework.component.core.util.BigDecimalUtil;
import com.sztus.framework.component.core.util.DateUtil;
import com.sztus.teldrassil.sprint.api.client.EmployeeApi;
import com.sztus.teldrassil.sprint.api.request.SearchEmployeeByConditionRequest;
import com.sztus.teldrassil.sprint.api.response.SearchEmployeeByConditionResponse;
import com.sztus.teldrassil.sprint.api.view.EmployeePersonalView;
import com.sztus.teldrassil.sprint.component.converter.SprintConverter;
import com.sztus.teldrassil.sprint.object.business.bo.RatioOfWorkloadBo;
import com.sztus.teldrassil.sprint.object.business.bo.SprintAndPlanIdBo;
import com.sztus.teldrassil.sprint.object.business.bo.TaskStatusCountBo;
import com.sztus.teldrassil.sprint.object.business.dto.TaskStatusCountDto;
import com.sztus.teldrassil.sprint.object.business.vo.TaskStatusCountVo;
import com.sztus.teldrassil.sprint.object.response.RatioOfWorkloadResponse;
import com.sztus.teldrassil.sprint.object.response.SprintPlanResponse;
import com.sztus.teldrassil.sprint.repository.reader.SprintJdbcReader;
import com.sztus.teldrassil.sprint.type.enumerate.SprintTaskStatusEnum;
import com.sztus.teldrassil.sprint.util.SprintUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Service
public class SprintService {

    private final SprintJdbcReader sprintJdbcReader;

    private final EmployeeApi employeeApi;

    public SprintService(SprintJdbcReader sprintJdbcReader, EmployeeApi employeeApi) {
        this.sprintJdbcReader = sprintJdbcReader;
        this.employeeApi = employeeApi;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SprintService.class);


    private static final List<Integer> NOT_IN_PROCESS_STATUS = new ArrayList<>(Arrays.asList(SprintTaskStatusEnum.CANCELLED.getValue(), SprintTaskStatusEnum.TODO.getValue(), SprintTaskStatusEnum.RELEASED.getValue(), SprintTaskStatusEnum.TOTAL.getValue()));

    private static final Long ONE_DAY = 24 * 60 * 60 * 1000L;

    public List<SprintPlanResponse> listSprint(Long lineId, Integer page, Integer size) {
        //查询sprint信息
        List<SprintAndPlanIdBo> sprintList = sprintJdbcReader.findSprintByLineId(lineId, page, size);
        if (CollectionUtils.isEmpty(sprintList)) {
            LOGGER.info("[SprintService->>listSprint]The query sprint is empty.lineId:{}", lineId);
            return Collections.EMPTY_LIST;
        }

        //统计数量 根据planId 分别查询task
        //按planId、状态分组。然后求和
        Set<Long> planIds = sprintList.stream().map(SprintAndPlanIdBo::getPlanId).collect(Collectors.toSet());
        List<TaskStatusCountBo> taskStatusCountBoList = getTaskStatusCount(planIds);

        return getSprintPlanResponseList(sprintList, taskStatusCountBoList);

    }

    /**
     * 根据planId集合  获取每个planId的状态数量 和进度条
     *
     * @param planIds planIds
     * @return 按planId分组 统计状态数量 和进度条
     */
    private List<TaskStatusCountBo> getTaskStatusCount(Set<Long> planIds) {
        List<TaskStatusCountDto> taskStatusCountDos = sprintJdbcReader.findTaskStatusCount(planIds);
        if (CollectionUtils.isEmpty(taskStatusCountDos)) {
            return Collections.emptyList();
        }
        List<TaskStatusCountBo> taskStatusCountBoList = new ArrayList<>(taskStatusCountDos.size());
        Map<Long, List<TaskStatusCountDto>> taskStatusGroup = taskStatusCountDos.stream().collect(Collectors.groupingBy(TaskStatusCountDto::getPlanId));
        for (Map.Entry<Long, List<TaskStatusCountDto>> taskTausCountMap : taskStatusGroup.entrySet()) {
            Long planId = taskTausCountMap.getKey();
            List<TaskStatusCountDto> statusCountDtos = taskTausCountMap.getValue();
            if (CollectionUtils.isEmpty(statusCountDtos)) {
                continue;
            }
            TaskStatusCountBo taskStatusCountBo = new TaskStatusCountBo();
            taskStatusCountBo.setPlanId(planId);

            //countOfCancelled
            Integer countOfCancelled = statusCountDtos.stream().filter(item -> SprintTaskStatusEnum.CANCELLED.getValue().equals(item.getStatus())).map(TaskStatusCountDto::getStatusCount).reduce(GlobalConst.INT_ZERO, Integer::sum);
            taskStatusCountBo.setCountOfCancelled(countOfCancelled);

            //countOfToDo
            Integer countOfToDo = statusCountDtos.stream().filter(item -> SprintTaskStatusEnum.TODO.getValue().equals(item.getStatus())).map(TaskStatusCountDto::getStatusCount).reduce(GlobalConst.INT_ZERO, Integer::sum);
            taskStatusCountBo.setCountOfToDo(countOfToDo);

            //countOfInProcess
            Integer countOfInProcess = statusCountDtos.stream().filter(item -> !NOT_IN_PROCESS_STATUS.contains(item.getStatus())).map(TaskStatusCountDto::getStatusCount).reduce(GlobalConst.INT_ZERO, Integer::sum);
            taskStatusCountBo.setCountOfInProcess(countOfInProcess);

            //countOfReleased
            Integer countOfReleased = statusCountDtos.stream().filter(item -> SprintTaskStatusEnum.RELEASED.getValue().equals(item.getStatus())).map(TaskStatusCountDto::getStatusCount).reduce(GlobalConst.INT_ZERO, Integer::sum);
            taskStatusCountBo.setCountOfReleased(countOfReleased);

            //countOfTotal
            Integer countOfTotal = countOfCancelled + countOfToDo + countOfInProcess + countOfReleased;
            taskStatusCountBo.setCountOfTotal(countOfTotal);

            //进度条  不计算取消的
            Integer calculateInProcess = statusCountDtos.stream().filter(item -> !NOT_IN_PROCESS_STATUS.contains(item.getStatus())).map(item -> SprintTaskStatusEnum.getCountByValue(item.getStatus()) * item.getStatusCount()).reduce(GlobalConst.INT_ZERO, Integer::sum);
            Integer ratioOfSprint = SprintTaskStatusEnum.TODO.getCount() * countOfToDo + calculateInProcess + SprintTaskStatusEnum.RELEASED.getCount() * countOfReleased;
            Integer ratioOfSprintCount = countOfTotal - countOfCancelled;
            if (ratioOfSprintCount == 0) {
                taskStatusCountBo.setRatioOfSprintTask(BigDecimal.ZERO);
            }
            taskStatusCountBo.setRatioOfSprintTask(BigDecimalUtil.div(ratioOfSprint, ratioOfSprintCount, 4, BigDecimal.ROUND_HALF_UP));

            taskStatusCountBoList.add(taskStatusCountBo);
        }
        return taskStatusCountBoList;
    }


    /**
     * 根据planId整和sprint基本信息和每个sprint的状态统计
     *
     * @param sprintList            sprint基本信息
     * @param taskStatusCountBoList 每个sprint的状态统计和进度条
     * @return 整合sprint基本信息、状态、进度条
     */
    private List<SprintPlanResponse> getSprintPlanResponseList(List<SprintAndPlanIdBo> sprintList, List<TaskStatusCountBo> taskStatusCountBoList) {
        if (CollectionUtils.isEmpty(taskStatusCountBoList)) {
            // 初始值为0
            TaskStatusCountVo taskStatusCountVo = initTaskStatusCountVo();

            return sprintList.stream().map(item -> SprintConverter.INSTANCE.sprintAndPlanIdBoToSprintPlanResponse(item, taskStatusCountVo)).collect(Collectors.toList());
        }

        List<SprintPlanResponse> sprintPlanResponseList = new ArrayList<>(sprintList.size());
        Map<Long, TaskStatusCountBo> taskStatusCountBoMap = taskStatusCountBoList.stream().collect(Collectors.toMap(TaskStatusCountBo::getPlanId, Function.identity()));
        for (SprintAndPlanIdBo sprintAndPlanIdBo : sprintList) {
            // 为空时，赋予默认值0
            TaskStatusCountVo taskStatusCountVo = SprintConverter.INSTANCE.taskStatusCountBoToTaskStatusCountVo(taskStatusCountBoMap.get(sprintAndPlanIdBo.getPlanId()));
            if (Objects.isNull(taskStatusCountVo)) {
                taskStatusCountVo = initTaskStatusCountVo();
            }
            SprintPlanResponse sprintPlanResponse = SprintConverter.INSTANCE.sprintAndPlanIdBoToSprintPlanResponse(sprintAndPlanIdBo, taskStatusCountVo);
            sprintPlanResponseList.add(sprintPlanResponse);
        }
        return sprintPlanResponseList;
    }

    private TaskStatusCountVo initTaskStatusCountVo() {
        TaskStatusCountVo taskStatusCountVo = new TaskStatusCountVo();
        taskStatusCountVo.setCountOfToDo(GlobalConst.INT_ZERO);
        taskStatusCountVo.setCountOfInProcess(GlobalConst.INT_ZERO);
        taskStatusCountVo.setCountOfCancelled(GlobalConst.INT_ZERO);
        taskStatusCountVo.setCountOfReleased(GlobalConst.INT_ZERO);
        taskStatusCountVo.setCountOfTotal(GlobalConst.INT_ZERO);
        taskStatusCountVo.setRatioOfSprintTask(BigDecimal.ZERO);
        return taskStatusCountVo;
    }

    public List getWorkload(Long lineId, List<Long> employeeList) throws ProcedureException {
        //根据lineId查询  employeeId和名字
        Map<Long, String> employeeIdAndName = getEmployeeIdAndName(lineId);
        if (!CollectionUtils.isEmpty(employeeList)) {
            employeeList.addAll(employeeIdAndName.keySet());
        } else {
            employeeList = new ArrayList<>(employeeIdAndName.keySet());
        }

        //当前时间
        Date currentDate = DateUtil.getCurrentDate();
        Long currentTime = currentDate.getTime();
        Long endTime = SprintUtil.atStartOfDay(currentDate, 8);

        List<RatioOfWorkloadBo> ratioOfWorkloadBos = getRatioOfWorkloadBo(employeeList, currentTime, endTime);
        if (CollectionUtils.isEmpty(ratioOfWorkloadBos)) {
            LOGGER.info("[SprintService-->>getRatioOfWorkloadBo]The query ratio Of Work load is empty.employeeIds:{},currentTime：{},endTime:{}", employeeList, currentTime, endTime);
            return Collections.EMPTY_LIST;
        }
        //按employeeId分组  并赋名字 和计算值
        List<RatioOfWorkloadResponse> ratioOfWorkloadResponseList = new ArrayList<>(ratioOfWorkloadBos.size());
        List<Long> stampWeek = new ArrayList<>(Arrays.asList(currentTime, currentTime + ONE_DAY, currentTime + 2 * ONE_DAY, currentTime + 3 * ONE_DAY, currentTime + 4 * ONE_DAY, currentTime + 5 * ONE_DAY, currentTime + 6 * ONE_DAY));
        Map<Long, List<RatioOfWorkloadBo>> ratioOfWorkloadGroup = ratioOfWorkloadBos.stream().collect(Collectors.groupingBy(RatioOfWorkloadBo::getEmployeeId));
        for (Long employeeId : ratioOfWorkloadGroup.keySet()) {
            List<RatioOfWorkloadBo> workloadBoList = ratioOfWorkloadGroup.get(employeeId);
            // 这是按凌晨处理的
            List<RatioOfWorkloadBo> ratioOfWorkloadBos1 = workloadBoList.stream().filter(item -> stampWeek.contains(item.getDate())).collect(Collectors.toList());

            RatioOfWorkloadResponse ratioOfWorkloadResponse = new RatioOfWorkloadResponse();
            ratioOfWorkloadResponse.setRatioOfWorkloadVoList(SprintConverter.INSTANCE.ratioOfWorkloadBosToRatioOfWorkloadVos(ratioOfWorkloadBos1));

            //ratioOfWorkload
            if (!CollectionUtils.isEmpty(ratioOfWorkloadBos1)) {
                ratioOfWorkloadResponse.setRatioOfWorkload(ratioOfWorkloadBos1.size() * 8);
            }

            //修改employee名字
            ratioOfWorkloadResponse.setEmployeeName(employeeIdAndName.get(employeeId));

            ratioOfWorkloadResponseList.add(ratioOfWorkloadResponse);
        }

        return ratioOfWorkloadResponseList;

    }

    /**
     * 根据lineId获取 employeeId和名字
     *
     * @param lineId departmentId
     * @return employeeId和名字
     * @throws ProcedureException
     */
    private Map<Long, String> getEmployeeIdAndName(Long lineId) throws ProcedureException {
        Map<Long, String> employeeIdAndName = new HashMap<>();
        SearchEmployeeByConditionRequest searchEmployeeByConditionRequest = new SearchEmployeeByConditionRequest();
        searchEmployeeByConditionRequest.setCompanyId(1L);
        SearchEmployeeByConditionResponse searchEmployeeByConditionResponse = employeeApi.searchEmployeeByCondition(searchEmployeeByConditionRequest);

        if (Objects.nonNull(searchEmployeeByConditionResponse) && !CollectionUtils.isEmpty(searchEmployeeByConditionResponse.getItems())) {
            searchEmployeeByConditionResponse.getItems().stream().filter(item -> Objects.nonNull(lineId) && lineId.equals(item.getDepartmentId())).forEach(item -> {
                Long id = item.getId();
                EmployeePersonalView personal = item.getPersonal();
                String fullName = "";
                if (Objects.nonNull(personal)) {
                    String firstName = personal.getFirstName();
                    String middleName = StringUtils.isBlank(personal.getMiddleName()) ? " " : " " + personal.getMiddleName() + " ";
                    String lastName = personal.getLastName();
                    fullName = firstName + middleName + lastName;
                }
                employeeIdAndName.put(id, fullName);
            });
        }
        LOGGER.info("[getRatioOfWorkloadBo-->>getEmployeeIdAndName]The Employee information obtained through the api is:{} .lineId:{}", JSON.toJSONString(employeeIdAndName, SerializerFeature.WriteMapNullValue), lineId);
        return employeeIdAndName;
    }

    private List<RatioOfWorkloadBo> getRatioOfWorkloadBo(List<Long> employeeList, long currentTime, Long endTime) {
        return sprintJdbcReader.findRatioOfWorkload(employeeList, currentTime, endTime);
    }

}
