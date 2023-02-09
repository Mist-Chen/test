package com.sztus.teldrassil.sprint.api.request;

public class SearchEmployeeByConditionRequest {
    private String employeeInfo;
    private Long siteId;
    private Long companyId;
    private Long positionId;
    private String feature;
    private String level;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getEmployeeInfo() {
        return employeeInfo;
    }

    public void setEmployeeInfo(String employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
