package com.sztus.teldrassil.sprint.type.enumerate;

import com.sztus.framework.component.core.base.BaseEnum;

/**
 * @Author free
 **/
public enum SprintTaskStatusEnum implements BaseEnum {


    CANCELLED(-1,"Cancelled"),
    TODO(0,"Todo"),
    DESIGNED(1,"Designed"),
    IMPLEMENTED(2,"Implemented"),
    TESTED(3,"Tested"),
    SETTLED(4,"Settled"),
    RELEASED(10,"Released"),
    ;


    private Integer value;
    private String text;

    SprintTaskStatusEnum(Integer value, String text) {
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
