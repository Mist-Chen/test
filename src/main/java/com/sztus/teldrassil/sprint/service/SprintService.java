package com.sztus.teldrassil.sprint.service;


import com.sztus.framework.component.core.constant.GlobalConst;
import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.framework.component.core.util.BigDecimalUtil;
import com.sztus.framework.component.core.util.DateUtil;
import com.sztus.teldrassil.sprint.api.client.EmployeeApi;
import com.sztus.teldrassil.sprint.api.request.SearchEmployeeByConditionRequest;
import com.sztus.teldrassil.sprint.api.response.SearchEmployeeByConditionResponse;
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

    private static List<Integer> IN_PROCESS_STATUS = new ArrayList<>(Arrays.asList(
            SprintTaskStatusEnum.CANCELLED.getValue(),
            SprintTaskStatusEnum.TODO.getValue(),
            SprintTaskStatusEnum.RELEASED.getValue()));

    private static Long ONE_DAY = 24 * 60 * 60 * 1000L;

    public List<SprintPlanResponse> listSprint(Long lineId) {
        //查询sprint信息
        List<SprintAndPlanIdBo> sprintList = sprintJdbcReader.findSprintByLineId(lineId);
        if (CollectionUtils.isEmpty(sprintList)) {
            return Collections.EMPTY_LIST;
        }

        //统计数量 根据planId 分别查询task
        //按planId、状态分组。然后求和
        Set<Long> planIds = sprintList.stream().map(SprintAndPlanIdBo::getPlanId).collect(Collectors.toSet());
        List<TaskStatusCountBo> taskStatusCountBoList = getTaskStatusCount(planIds);

        return getSprintPlanResponseList(sprintList, taskStatusCountBoList);

    }

    private List<TaskStatusCountBo> getTaskStatusCount(Set<Long> planIds) {
        List<TaskStatusCountDto> taskStatusCountDtos = sprintJdbcReader.findTaskStatusCount(planIds);
        if (CollectionUtils.isEmpty(taskStatusCountDtos)) {
            return Collections.EMPTY_LIST;
        }
        List<TaskStatusCountBo> taskStatusCountBoList = new ArrayList<>(taskStatusCountDtos.size());
        Map<Integer, List<TaskStatusCountDto>> taskStatusGroup = taskStatusCountDtos.stream().collect(Collectors.groupingBy(TaskStatusCountDto::getPlanId));
        for (Integer planId : taskStatusGroup.keySet()) {
            List<TaskStatusCountDto> statusCountDtos = taskStatusGroup.get(planId);
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
            Integer countOfInProcess = statusCountDtos.stream().filter(item -> IN_PROCESS_STATUS.contains(item.getStatus())).map(TaskStatusCountDto::getStatusCount).reduce(GlobalConst.INT_ZERO, Integer::sum);
            taskStatusCountBo.setCountOfInProcess(countOfInProcess);

            //countOfReleased
            Integer countOfReleased = statusCountDtos.stream().filter(item -> SprintTaskStatusEnum.RELEASED.getValue().equals(item.getStatus())).map(TaskStatusCountDto::getStatusCount).reduce(GlobalConst.INT_ZERO, Integer::sum);
            taskStatusCountBo.setCountOfReleased(countOfReleased);

            //countOfTotal
            Integer countOfTotal = countOfCancelled + countOfToDo + countOfInProcess + countOfReleased;
            taskStatusCountBo.setCountOfTotal(countOfTotal);

            //进度条  不计算取消的
            Integer calculateInProcess = statusCountDtos.stream().filter(item -> IN_PROCESS_STATUS.contains(item.getStatus())).map(item -> SprintTaskStatusEnum.getCountByValue(item.getStatus()) * item.getStatusCount()).reduce(GlobalConst.INT_ZERO, Integer::sum);
            Integer ratioOfSprint = SprintTaskStatusEnum.TODO.getCount() * countOfToDo + calculateInProcess + SprintTaskStatusEnum.RELEASED.getCount() * countOfReleased;
            Integer ratioOfSprintCount = countOfTotal - countOfCancelled;
            if (ratioOfSprintCount == 0) {
                taskStatusCountBo.setRatioOfSprintTask(BigDecimal.ZERO);
            }
            taskStatusCountBo.setRatioOfSprintTask(BigDecimalUtil.div(ratioOfSprint, ratioOfSprintCount));

            taskStatusCountBoList.add(taskStatusCountBo);
        }
        return taskStatusCountBoList;
    }


    /**
     * 根据planId计算每个的task状态
     *
     * @param sprintList
     * @param taskStatusCountBoList
     * @return
     */
    private List<SprintPlanResponse> getSprintPlanResponseList(List<SprintAndPlanIdBo> sprintList, List<TaskStatusCountBo> taskStatusCountBoList) {
        if (CollectionUtils.isEmpty(taskStatusCountBoList)) {
            // 初始值为0
            TaskStatusCountVo taskStatusCountVo = initTaskStatusCountVo();

            return sprintList.stream()
                    .map(SprintConverter.INSTANCE::sprintAndPlanIdBoToSprintPlanResponse)
                    .peek(item -> item.setTaskStatusCountVo(taskStatusCountVo))
                    .collect(Collectors.toList());
        }

        List<SprintPlanResponse> sprintPlanResponseList = new ArrayList<>(sprintList.size());
        Map<Integer, TaskStatusCountBo> taskStatusCountBoMap = taskStatusCountBoList.stream().collect(Collectors.toMap(TaskStatusCountBo::getPlanId, Function.identity()));
        for (SprintAndPlanIdBo sprintAndPlanIdBo : sprintList) {
            SprintPlanResponse sprintPlanResponse = SprintConverter.INSTANCE.sprintAndPlanIdBoToSprintPlanResponse(sprintAndPlanIdBo);
            // 为空时，赋予默认值0
            TaskStatusCountVo taskStatusCountVo = SprintConverter.INSTANCE.taskStatusCountBoToTaskStatusCountVo(taskStatusCountBoMap.get(sprintAndPlanIdBo.getPlanId()));
            if (Objects.isNull(taskStatusCountVo)) {
                taskStatusCountVo = initTaskStatusCountVo();
            }
            sprintPlanResponse.setTaskStatusCountVo(taskStatusCountVo);
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

    public List<RatioOfWorkloadResponse> getWorkload(Long lineId, List<Long> employeeList) throws ProcedureException {
        //根据lineId查询  employeeId
        SearchEmployeeByConditionRequest searchEmployeeByConditionRequest = new SearchEmployeeByConditionRequest();
        searchEmployeeByConditionRequest.setCompanyId(1L);
        SearchEmployeeByConditionResponse searchEmployeeByConditionResponse = employeeApi.searchEmployeeByCondition(searchEmployeeByConditionRequest);
        if (Objects.isNull(searchEmployeeByConditionResponse)) {

        }

        //当前时间
        Date currentDate = DateUtil.getCurrentDate();
        Long currentTime = currentDate.getTime();
        Long endTime = SprintUtil.atStartOfDay(currentDate, 8);

        List<RatioOfWorkloadBo> ratioOfWorkloadBos = getRatioOfWorkloadBo(employeeList, currentTime, endTime);
        if (CollectionUtils.isEmpty(ratioOfWorkloadBos)) {
            return Collections.EMPTY_LIST;
        }
        //按employeeId分组  并赋名字 和计算值
        List<RatioOfWorkloadResponse> ratioOfWorkloadResponseList = new ArrayList<>(ratioOfWorkloadBos.size());
        List<Long> stampWeek = new ArrayList<>(Arrays.asList(currentTime, currentTime + ONE_DAY, currentTime + 2 * ONE_DAY, currentTime + 3 * ONE_DAY, currentTime + 4 * ONE_DAY, currentTime + 5 * ONE_DAY, currentTime + 6 * ONE_DAY));
        Map<Long, List<RatioOfWorkloadBo>> ratioOfWorkloadGroup = ratioOfWorkloadBos.stream().collect(Collectors.groupingBy(RatioOfWorkloadBo::getEmployeeId));
        for (Long employeeId : ratioOfWorkloadGroup.keySet()) {
            List<RatioOfWorkloadBo> workloadBoList = ratioOfWorkloadGroup.get(employeeId);
            //todo 这是按凌晨处理的
            List<RatioOfWorkloadBo> ratioOfWorkloadBos1 = workloadBoList.stream().filter(item -> stampWeek.contains(item.getDate())).collect(Collectors.toList());

            RatioOfWorkloadResponse ratioOfWorkloadResponse = new RatioOfWorkloadResponse();
            ratioOfWorkloadResponse.setRatioOfWorkloadVoList(SprintConverter.INSTANCE.ratioOfWorkloadBosToRatioOfWorkloadVos(ratioOfWorkloadBos1));

            //ratioOfWorkload
            if (!CollectionUtils.isEmpty(ratioOfWorkloadBos1)) {
                ratioOfWorkloadResponse.setRatioOfWorkload(ratioOfWorkloadBos1.size() * 8);
            }

            //修改employee名字

            ratioOfWorkloadResponseList.add(ratioOfWorkloadResponse);
        }

        return ratioOfWorkloadResponseList;

    }

    private List<RatioOfWorkloadBo> getRatioOfWorkloadBo(List<Long> employeeList, long currentTime, Long endTime) {
        return sprintJdbcReader.findRatioOfWorkload(employeeList, currentTime, endTime);
    }

}
