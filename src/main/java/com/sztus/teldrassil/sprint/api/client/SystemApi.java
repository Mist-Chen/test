package com.sztus.teldrassil.sprint.api.client;

import com.sztus.teldrassil.sprint.api.response.GetSystemListResponse;
import com.sztus.teldrassil.sprint.type.constant.SprintActionConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 13:59
 * @DES:
 */
@FeignClient(value = "dalaran-system", path = "/v4")
public interface SystemApi {

    @GetMapping(SprintActionConstant.SYSTEM_ALL_LIST)
    GetSystemListResponse allListSystem();

}
