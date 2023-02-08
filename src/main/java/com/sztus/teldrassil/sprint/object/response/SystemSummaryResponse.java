package com.sztus.teldrassil.sprint.object.response;

import com.sztus.teldrassil.sprint.object.view.TaskInfoView;
import lombok.Data;

import java.util.ArrayList;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 15:55
 * @DES:
 */
@Data
public class SystemSummaryResponse {

    private String systemName;

    private Integer ratioOfSystemTask;

    private ArrayList<TaskInfoView> taskInfo;
}
