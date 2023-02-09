package com.sztus.teldrassil.sprint.api.view;

public class EmployeeExtensionView {

    private Long employeeId;
    private String extKey;
    private String extValue;

    public EmployeeExtensionView() {
    }

    public EmployeeExtensionView(String extKey, String extValue) {
        this.extKey = extKey;
        this.extValue = extValue;
    }
    public EmployeeExtensionView(Long employeeId,String extKey, String extValue) {
        this.employeeId = employeeId;
        this.extKey = extKey;
        this.extValue = extValue;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getExtKey() {
        return extKey;
    }

    public void setExtKey(String extKey) {
        this.extKey = extKey;
    }

    public String getExtValue() {
        return extValue;
    }

    public void setExtValue(String extValue) {
        this.extValue = extValue;
    }
}
