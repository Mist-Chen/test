package com.sztus.teldrassil.sprint.type.enumerate;

import com.sztus.framework.component.core.base.BaseEnum;

/**
 * @Author free
 **/
public enum SprintTaskStatusEnum implements BaseEnum {

    CANCELLED(-1, "Cancelled", 0),
    TODO(0, "Todo", 0),
    DESIGNED(1, "Designed", 20),
    IMPLEMENTED(2, "Implemented", 40),
    TESTED(3, "Tested", 60),
    SETTLED(4, "Settled", 80),
    RELEASED(10, "Released", 100),
    TOTAL(11, "Total", 0),
    ;

    private Integer value;
    private String text;
    private Integer count;

    SprintTaskStatusEnum(Integer value, String text, Integer count) {
        this.value = value;
        this.text = text;
        this.count = count;
    }

    public static Integer getCountByValue(Integer value) {
        for (SprintTaskStatusEnum sprintTaskStatusEnum : values()) {
            if (sprintTaskStatusEnum.getValue().equals(value)) {
                return sprintTaskStatusEnum.getCount();
            }
        }
        return 0;

    }

    public Integer getCount() {
        return count;
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
