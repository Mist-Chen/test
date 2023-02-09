package com.sztus.teldrassil.sprint.object.business.dto;

import lombok.Data;

/**
 * @Author free
 **/
@Data
public class TaskStatusCountDto {
    private Long planId;

    private Integer status;

    private Integer statusCount;

}
