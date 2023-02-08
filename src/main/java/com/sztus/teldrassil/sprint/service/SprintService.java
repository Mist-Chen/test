package com.sztus.teldrassil.sprint.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sztus.framework.component.database.type.SqlOption;
import com.sztus.teldrassil.sprint.api.client.EmployeeApi;
import com.sztus.teldrassil.sprint.api.client.SystemApi;
import com.sztus.teldrassil.sprint.api.response.GetSystemListResponse;
import com.sztus.teldrassil.sprint.api.view.SystemView;
import com.sztus.teldrassil.sprint.component.converter.SprintConverter;
import com.sztus.teldrassil.sprint.object.domain.*;
import com.sztus.teldrassil.sprint.object.response.SprintTaskInfoResponse;
import com.sztus.teldrassil.sprint.object.response.SystemSummaryResponse;
import com.sztus.teldrassil.sprint.object.view.SystemViewForSprint;
import com.sztus.teldrassil.sprint.object.view.TaskInfoView;
import com.sztus.teldrassil.sprint.object.view.TaskView;
import com.sztus.teldrassil.sprint.repository.cache.SprintRedisRepository;
import com.sztus.teldrassil.sprint.repository.reader.SprintJdbcReader;
import com.sztus.teldrassil.sprint.type.constant.DbKeyConstant;
import com.sztus.teldrassil.sprint.type.constant.SprintCacheKey;
import com.sztus.teldrassil.sprint.type.enumerate.SprintTaskStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.sztus.teldrassil.sprint.type.enumerate.SprintTaskStatusEnum.*;

/**
 * @author Administrator
 */
@Service
public class SprintService {

    private final SprintJdbcReader sprintJdbcReader;
    private final SprintRedisRepository sprintRedisRepository;
    private final SystemApi systemApi;
    private final EmployeeApi employeeApi;

    private static final Logger LOGGER = LoggerFactory.getLogger(SprintService.class);

    public SprintService(SprintJdbcReader sprintJdbcReader, SprintRedisRepository sprintRedisRepository, SystemApi systemApi, EmployeeApi employeeApi) {
        this.sprintJdbcReader = sprintJdbcReader;
        this.sprintRedisRepository = sprintRedisRepository;
        this.systemApi = systemApi;
        this.employeeApi = employeeApi;
    }

    public SprintTaskInfoResponse listSprintTaskBySprintIdAndDepartmentId(Long sprintId, Integer lineId) {

        List<Long> taskIds = getTaskIds(sprintId, lineId);
        if (CollectionUtils.isEmpty(taskIds)) {
            return null;
        }

        SqlOption taskOption = SqlOption.getInstance().whereIN(DbKeyConstant.ID, taskIds);
        List<SprintTask> taskList = sprintJdbcReader.findAllByOptions(SprintTask.class, taskOption.toString());

        SqlOption taskContentOption = SqlOption.getInstance().whereIN(DbKeyConstant.TASK_ID, taskIds);
        List<SprintTaskContent> contentList = sprintJdbcReader.findAllByOptions(SprintTaskContent.class, taskContentOption.toString());
        Map<Long, SprintTaskContent> contentMap = contentList.stream().collect(Collectors.toMap(SprintTaskContent::getTaskId, o -> o));

        SqlOption employeeOption = SqlOption.getInstance().whereIN(DbKeyConstant.TASK_ID, taskIds);
        List<SprintTaskAssignee> employeeList = sprintJdbcReader.findAllByOptions(SprintTaskAssignee.class, employeeOption.toString());
        Map<Long, SprintTaskAssignee> taskAssigneeMap = employeeList.stream().collect(Collectors.toMap(SprintTaskAssignee::getTaskId, O -> O));

        ArrayList<TaskView> items = new ArrayList<>();
        taskList.forEach(sprintTask -> {
            Long taskId = sprintTask.getId();
            String systemNameFromRedis = getSystemNameFromRedis(sprintTask.getSystemId());
            SprintTaskAssignee sprintTaskAssignee = taskAssigneeMap.get(taskId);

            getEmployeeNameByClient(sprintTaskAssignee.getEmployeeId());
            TaskView taskView = SprintConverter.INSTANCE.sprintTaskAndContentToTaskView(sprintTask, contentMap.get(taskId));
            taskView.setSystemName(systemNameFromRedis);
            items.add(taskView);
        });

        SprintTaskInfoResponse sprintTaskInfoResponse = new SprintTaskInfoResponse();
        sprintTaskInfoResponse.setTaskItems(items);

        return sprintTaskInfoResponse;
    }

    private void getEmployeeNameByClient(Long employeeId) {


    }

    private String getSystemNameFromRedis(Long systemId) {
        String systemStr = sprintRedisRepository.get(sprintRedisRepository.generateKey(SprintCacheKey.SYSTEM, systemId));
        JSONObject jsonObject = JSON.parseObject(systemStr);
        return jsonObject.get(SprintCacheKey.SYSTEM_NAME).toString();
    }

    private List<Long> getTaskIds(Long sprintId, Integer lineId) {
        SqlOption option = SqlOption.getInstance().whereEqual(DbKeyConstant.SPRINT_ID, sprintId).select(DbKeyConstant.ID);
        if (Objects.isNull(lineId)) {
            option.whereEqual(DbKeyConstant.LINE_ID, lineId);
        }

        List<SprintPlan> planList = sprintJdbcReader.findAllByOptions(SprintPlan.class, option.toString());
        if (CollectionUtils.isEmpty(planList)) {
            return Collections.emptyList();
        }

        List<Long> planIds = planList.stream().map(SprintPlan::getId).collect(Collectors.toList());
        SqlOption relationOption = SqlOption.getInstance().whereIN(DbKeyConstant.PLAN_ID, planIds);
        List<RelationSprintTaskToPlan> allByOptions = sprintJdbcReader.findAllByOptions(RelationSprintTaskToPlan.class, relationOption.toString());
        if (CollectionUtils.isEmpty(allByOptions)) {
            return Collections.emptyList();
        }

        return allByOptions
                .stream()
                .map(RelationSprintTaskToPlan::getTaskId)
                .collect(Collectors.toList());
    }

    public List<SystemSummaryResponse> systemSummaryQueryBySprintIdAndDepartmentId(Long sprintId, Integer lineId) {

        List<Long> taskIds = getTaskIds(sprintId, lineId);
        if (CollectionUtils.isEmpty(taskIds)) {
            return null;
        }

        SqlOption taskOption = SqlOption.getInstance().whereIN(DbKeyConstant.ID, taskIds);
        List<SprintTask> taskList = sprintJdbcReader.findAllByOptions(SprintTask.class, taskOption.toString());
        Map<Long, List<SprintTask>> listMap = taskList.stream().collect(Collectors.groupingBy(SprintTask::getSystemId));

        List<Long> systemIds = taskList.stream().map(SprintTask::getSystemId).collect(Collectors.toList());
        ArrayList<SystemSummaryResponse> summaryResponses = new ArrayList<>();
        systemIds.forEach(systemId -> {
            SystemSummaryResponse response = new SystemSummaryResponse();
            ArrayList<TaskInfoView> taskInfoViews = new ArrayList<>();
            List<SprintTask> sprintTasks = listMap.get(systemId);
            Map<Integer, List<SprintTask>> statusMap = sprintTasks.stream().collect(Collectors.groupingBy(SprintTask::getStatus));

            for (SprintTaskStatusEnum taskStatusEnum : SprintTaskStatusEnum.values()) {
                List<SprintTask> list = statusMap.get(taskStatusEnum.getValue());
                TaskInfoView taskInfoView = new TaskInfoView();
                taskInfoView.setCount(list.size());

                switch (taskStatusEnum) {
                    case CANCELLED:
                    case TODO:
                    case RELEASED:
                        taskInfoView.setTaskStatus(taskStatusEnum.getText());
                        break;
                    case DESIGNED:
                    case IMPLEMENTED:
                    case TESTED:
                    case SETTLED:
                        taskInfoView.setTaskStatus(SprintCacheKey.IN_PROGRESS);
                        break;
                    default:
                        break;
                }

                taskInfoViews.add(taskInfoView);
            }

            TaskInfoView total = new TaskInfoView();
            total.setTaskStatus(SprintCacheKey.TOTAL);
            total.setCount(sprintTasks.size());
            taskInfoViews.add(total);

            response.setTaskInfo(taskInfoViews);
            response.setSystemName(getSystemNameFromRedis(systemId));
            response.setRatioOfSystemTask(Integer.valueOf(calculateScore(statusMap)));
            summaryResponses.add(response);
        });
        return summaryResponses;
    }


    private String calculateScore(Map<Integer, List<SprintTask>> statusMap) {

        int designTasks = statusMap.get(DESIGNED.getValue()).size();
        int implementedTasks = statusMap.get(IMPLEMENTED.getValue()).size();
        int testedTasks = statusMap.get(TESTED.getValue()).size();
        int settledTasks = statusMap.get(SETTLED.getValue()).size();
        int releasedTasks = statusMap.get(RELEASED.getValue()).size();

        double number = designTasks * 0.2 + implementedTasks * 0.4 + testedTasks * 0.6 + settledTasks * 0.8 + releasedTasks;

        double score = number / designTasks + implementedTasks + testedTasks + settledTasks + releasedTasks;
        return String.valueOf(score);

    }

    public List<SystemViewForSprint> systemListQuery() {

        try {
            GetSystemListResponse response = systemApi.allListSystem();
            List<SystemView> items = response.getItems();
            ArrayList<SystemViewForSprint> viewForSprints = new ArrayList<>();
            items.forEach(systemView -> {
                SystemViewForSprint viewForSprint = new SystemViewForSprint();
                viewForSprint.setSystemId(systemView.getId());
                viewForSprint.setSystemName(systemView.getSystemName());
                viewForSprints.add(viewForSprint);
            });
            return viewForSprints;
        } catch (Exception e) {
            LOGGER.error("[systemListQuery] >>> System API Call Error!", e);
            return Collections.emptyList();
        }
    }
}
