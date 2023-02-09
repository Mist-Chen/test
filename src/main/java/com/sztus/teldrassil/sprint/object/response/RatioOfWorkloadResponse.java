package com.sztus.teldrassil.sprint.object.response;

import com.sztus.teldrassil.sprint.object.business.vo.RatioOfWorkloadVo;
import lombok.Data;

import java.util.List;

/**
 * @Author free
 **/
@Data
public class RatioOfWorkloadResponse {
    private String employeeName;

    private List<RatioOfWorkloadVo> ratioOfWorkloadVoList;

    private Integer ratioOfWorkload;
}
