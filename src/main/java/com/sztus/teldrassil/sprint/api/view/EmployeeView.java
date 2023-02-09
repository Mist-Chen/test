package com.sztus.teldrassil.sprint.api.view;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class EmployeeView {

    private Long id;

    @NotNull(message = "company is required")
    private Long companyId;

    @NotNull(message = "site is required")
    private Long siteId;

    private Long departmentId;

    private String openId;

    @NotNull(message = "position is required")
    private Long positionId;

    @NotEmpty(message = "employee no is required")
    private String employeeNo;

    private Integer status;

    private Long createdAt;

    private Long updatedAt;

    private EmployeeProfileView profile;

    private EmployeePositionView position;

    private List<EmployeeExtensionView> extensions;

    @Valid
    private EmployeePersonalView personal;

    public EmployeePersonalView getPersonal() {
        return personal;
    }

    public void setPersonal(EmployeePersonalView personal) {
        this.personal = personal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public EmployeeProfileView getProfile() {
        return profile;
    }

    public void setProfile(EmployeeProfileView profile) {
        this.profile = profile;
    }

    public EmployeePositionView getPosition() {
        return position;
    }

    public void setPosition(EmployeePositionView position) {
        this.position = position;
    }

    public List<EmployeeExtensionView> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<EmployeeExtensionView> extensions) {
        this.extensions = extensions;
    }
}