package com.sztus.teldrassil.sprint.object.response;

import com.sztus.teldrassil.sprint.object.view.TaskView;
import lombok.Data;

import java.util.ArrayList;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 11:14
 * @DES:
 */
@Data
public class SprintTaskInfoResponse {

    private Integer countOfCancelled;

    private Integer countOfToDo;

    private Integer countOfInProcess;

    private Integer countOfReleased;

    private Integer countOfTotal;

    private Integer ratioOfSprintTask;

    private ArrayList<TaskView> taskItems;
}
