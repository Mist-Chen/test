package com.sztus.teldrassil.sprint.type.enumerate;

import com.sztus.framework.component.core.base.BaseError;

/**
 * @author free
 */

public enum SprintErrorCode implements BaseError {

    DATA_SAVE_ERROR(-83050302, "Data save error."),

    ;

    SprintErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private final Integer code;
    private final String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
