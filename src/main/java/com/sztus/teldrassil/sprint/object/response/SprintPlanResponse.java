package com.sztus.teldrassil.sprint.object.response;

import lombok.Data;

/**
 * @Author free
 **/
@Data
public class SprintPlanResponse {
    private Long id;

    private String name;

    private String dueDate;

    private Integer status;

    private String startDate;

    private Integer ratioOfSprintTask;

    private Integer countOfToDo;

    private Integer countOfInProcess;

    private Integer countOfCancelled;

    private Integer countOfReleased;

    private Integer countOfTotal;

}
