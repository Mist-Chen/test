package com.sztus.teldrassil.sprint.object.business.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author free
 **/
@Data
public class TaskStatusCountBo {
    private Long planId;

    private Integer countOfToDo;

    private Integer countOfInProcess;

    private Integer countOfCancelled;

    private Integer countOfReleased;

    private Integer countOfTotal;

    private BigDecimal ratioOfSprintTask;


}
