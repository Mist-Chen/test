package com.sztus.teldrassil.sprint.controller;


import com.sztus.framework.component.core.type.AjaxResult;
import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.teldrassil.sprint.object.response.RatioOfWorkloadResponse;
import com.sztus.teldrassil.sprint.object.response.SprintPlanResponse;
import com.sztus.teldrassil.sprint.service.SprintService;
import com.sztus.teldrassil.sprint.type.constant.SprintActionConstant;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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

    @GetMapping(SprintActionConstant.LIST_SPRINT)
    public List<SprintPlanResponse> listSprint(@RequestParam Long lineId) {
        return sprintService.listSprint(lineId);
    }


    @GetMapping(SprintActionConstant.GET_WORKLOAD)
    public String getWorkload(@RequestParam Long lineId, @RequestBody List<Long> employeeId) throws ParseException, ProcedureException {
        return AjaxResult.success(sprintService.getWorkload(lineId, employeeId));
    }


}
