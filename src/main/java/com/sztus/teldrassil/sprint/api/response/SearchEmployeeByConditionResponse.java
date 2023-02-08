package com.sztus.teldrassil.sprint.api.response;


import com.sztus.teldrassil.sprint.api.view.EmployeeView;

import java.util.List;

public class SearchEmployeeByConditionResponse {
    private List<EmployeeView> items;

    public List<EmployeeView> getItems() {
        return items;
    }

    public void setItems(List<EmployeeView> items) {
        this.items = items;
    }
}
