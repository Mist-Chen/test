package com.sztus.teldrassil.sprint.type.enumerate;

import com.sztus.framework.component.core.base.BaseEnum;

/**
 * @Author free
 **/
public enum SprintPlanStatusEnum implements BaseEnum {
    CANCELLED(-1,"Cancelled"),
    NEW(0,"New"),
    IN_PROGRESS(1,"In Progress"),
    RELEASED(10,"Released"),
    ;


    private Integer value;
    private String text;

    SprintPlanStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }
}
