package com.sztus.teldrassil.sprint.controller;


import com.sztus.framework.component.core.type.AjaxResult;
import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.teldrassil.sprint.service.SprintService;
import com.sztus.teldrassil.sprint.type.constant.SprintActionConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String listSprint(@RequestParam(required = false) Long lineId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return AjaxResult.success(sprintService.listSprint(lineId, page, size));
    }


    @GetMapping(SprintActionConstant.GET_WORKLOAD)
    public String getWorkload(@RequestParam(required = false) Long lineId, @RequestParam(required = false) List<Long> employeeId) throws ParseException, ProcedureException {
        return AjaxResult.success(sprintService.getWorkload(lineId, employeeId));
    }


}
