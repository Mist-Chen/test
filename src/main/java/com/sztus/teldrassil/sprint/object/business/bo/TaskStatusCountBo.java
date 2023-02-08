package com.sztus.teldrassil.sprint.object.business.bo;

import lombok.Data;

/**
 * @Author free
 **/
@Data
public class TaskStatusCountBo {
    private Integer planId;

    private Integer countOfToDo;

    private Integer countOfInProcess;

    private Integer countOfCancelled;

    private Integer countOfReleased;

    private Integer countOfTotal;


}
