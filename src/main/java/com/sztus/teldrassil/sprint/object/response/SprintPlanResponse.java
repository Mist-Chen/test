package com.sztus.teldrassil.sprint.object.response;

import com.sztus.teldrassil.sprint.object.business.vo.TaskStatusCountVo;
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

    private TaskStatusCountVo taskStatusCountVo;
}
