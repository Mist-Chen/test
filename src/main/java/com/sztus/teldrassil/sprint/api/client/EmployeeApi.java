package com.sztus.teldrassil.sprint.api.client;

import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.teldrassil.sprint.api.request.SearchEmployeeByConditionRequest;
import com.sztus.teldrassil.sprint.api.response.SearchEmployeeByConditionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author free
 **/
@FeignClient(value = "dalaran-employee", path = "/v4")

public interface EmployeeApi {
    @PostMapping("/employee/search-by-condition")
    SearchEmployeeByConditionResponse searchEmployeeByCondition(
            @RequestBody SearchEmployeeByConditionRequest request) throws ProcedureException;

}
