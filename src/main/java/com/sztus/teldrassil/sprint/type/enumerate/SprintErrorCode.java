package com.sztus.teldrassil.sprint.type.enumerate;

import com.sztus.framework.component.core.base.BaseError;

/**
 * @author free
 */

public enum SprintErrorCode implements BaseError {

    // 参数校验异常
    DATA_SAVE_ERROR(-83050302, "Data save error."),
    PARAMETER_CHECK_ERROR(-95540001,"This parameter check error !")
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
