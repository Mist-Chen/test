package com.sztus.teldrassil.sprint.controller;


import com.alibaba.nacos.common.utils.Objects;
import com.sztus.framework.component.core.type.AjaxResult;
import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.teldrassil.sprint.object.response.SprintTaskInfoResponse;
import com.sztus.teldrassil.sprint.object.response.SystemSummaryResponse;
import com.sztus.teldrassil.sprint.object.view.SystemViewForSprint;
import com.sztus.teldrassil.sprint.service.SprintService;
import com.sztus.teldrassil.sprint.type.constant.SprintActionConstant;
import com.sztus.teldrassil.sprint.type.enumerate.SprintErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author free
 */
@RestController
@RequestMapping(SprintActionConstant.ROOT)
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }


    @GetMapping(SprintActionConstant.GET_TASK_INFO)
    public String listSprintTask(@RequestParam("sprintId") Long sprintId, @RequestParam("lineId") Integer lineId) throws ProcedureException {

        if (Objects.isNull(sprintId)) {
            throw new ProcedureException(SprintErrorCode.PARAMETER_CHECK_ERROR);
        }

        SprintTaskInfoResponse response = sprintService.listSprintTaskBySprintIdAndDepartmentId(sprintId, lineId);
        return AjaxResult.success(response);
    }

    @GetMapping(SprintActionConstant.GET_SYSTEM_SUMMARY)
    public String systemSummaryQuery(@RequestParam("sprintId") Long sprintId, @RequestParam("lineId") Integer lineId) throws ProcedureException {

        if (Objects.isNull(sprintId)) {
            throw new ProcedureException(SprintErrorCode.PARAMETER_CHECK_ERROR);
        }

        List<SystemSummaryResponse> responseArrayList = sprintService.systemSummaryQueryBySprintIdAndDepartmentId(sprintId, lineId);
        return AjaxResult.success(responseArrayList);
    }

    @GetMapping(SprintActionConstant.LIST_SYSTEM)
    public String systemListQuery() {

        List<SystemViewForSprint> viewForSprints = sprintService.systemListQuery();
        return AjaxResult.success(viewForSprints);
    }

}
