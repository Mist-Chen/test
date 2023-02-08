package com.sztus.teldrassil.sprint.object.business.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author free
 **/
@Data
public class TaskStatusCountVo {
    private Integer countOfToDo;

    private Integer countOfInProcess;

    private Integer countOfCancelled;

    private Integer countOfReleased;

    private Integer countOfTotal;

    private BigDecimal ratioOfSprintTask;


}
