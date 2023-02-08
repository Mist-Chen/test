package com.sztus.teldrassil.sprint.api.response;


import com.sztus.teldrassil.sprint.api.view.SystemView;
import lombok.Data;

import java.util.List;


/**
 * @author Tyler
 * @Date 22/3/1
 */
@Data
public class GetSystemListResponse  {
    private Integer count;
    private List<SystemView> items;
    private String result;


}
