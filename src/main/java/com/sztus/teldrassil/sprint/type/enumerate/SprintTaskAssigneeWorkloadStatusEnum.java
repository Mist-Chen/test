package com.sztus.teldrassil.sprint.type.enumerate;

import com.sztus.framework.component.core.base.BaseEnum;

/**
 * @Author free
 **/
public enum SprintTaskAssigneeWorkloadStatusEnum implements BaseEnum {


    VOIDED(-1,"Voided"),
    NONE(0,"None"),
    PLANNED(1,"Planned"),
    EFFECTED(10,"Effected"),
    ;


    private Integer value;
    private String text;

    SprintTaskAssigneeWorkloadStatusEnum(Integer value, String text) {
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
