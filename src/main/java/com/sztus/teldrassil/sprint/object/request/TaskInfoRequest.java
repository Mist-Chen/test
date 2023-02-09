package com.sztus.teldrassil.sprint.object.request;

import lombok.Data;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 18:46
 * @DES:
 */
@Data
public class TaskInfoRequest {

    private Long lineId;

    private Long sprintId;

    private Integer page;

    private Integer size;
}
