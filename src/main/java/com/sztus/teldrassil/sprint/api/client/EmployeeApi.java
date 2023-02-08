package com.sztus.teldrassil.sprint.api.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Mist-Chen
 * @Create: 2023/2/8 17:44
 * @DES:
 */
@FeignClient(value = "dalaran-employee", path = "/v4")
public interface EmployeeApi {


}
