package com.sztus.teldrassil.sprint.object.view;

import lombok.Data;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 11:17
 * @DES:
 */
@Data
public class TaskView {

    private Long id;

    private String systemName;

    private String taskNo;

    private Integer priority;

    private Long startDate;

    private Long dueDate;

    private Integer status;

    private String description;

    private String subject;

    private String employeeName;
}
