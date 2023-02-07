package com.sztus.teldrassil.sprint.component.handler;

import com.alibaba.fastjson.JSONObject;
import com.sztus.framework.component.core.constant.GlobalConst;
import com.sztus.framework.component.core.type.AjaxResult;
import com.sztus.framework.component.core.type.ProcedureException;
import com.sztus.framework.component.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

/**
 * @author free
 */
@ControllerAdvice
public class SprintExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SprintExceptionHandler.class);

    /**
     * 处理基类异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String exceptionHandler(HttpServletRequest request, Exception exception) {
        JSONObject resultJson = generateExceptionInfo(HttpStatus.INTERNAL_SERVER_ERROR, request, exception);
        return resultJson.toJSONString();
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = ProcedureException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String exceptionHandler(HttpServletRequest request, ProcedureException exception) {
        return AjaxResult.failure(exception);
    }

    /**
     * 获取异常的堆栈信息
     *
     * @param throwable 获取的异常父类
     * @return 堆栈信息
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }

    private JSONObject generateExceptionInfo(HttpStatus httpStatus, HttpServletRequest request, Exception exception) {
        String resultMessage = "";
        if (null != exception.getMessage()) {
            resultMessage = resultMessage + exception.getMessage();
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("path", request.getRequestURI());
        resultJson.put("arguments", resultMessage);
        resultJson.put("timestamp", DateUtil.dateToStr(new Date()));
        resultJson.put("error", httpStatus.getReasonPhrase());
        resultJson.put("status", httpStatus.value());
        resultJson.put("headers", request.getHeader("List-Headers"));
        logger.error(" {} was catch : {} info :{}", exception.getClass().getSimpleName(), getStackTrace(exception), resultJson.toJSONString());
        return resultJson;
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String exceptionHandler(BindException exception) {
        String exceptionMessage = GlobalConst.STR_EMPTY;

        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : allErrors) {
                FieldError fieldError = (FieldError) error;
                String defaultMessage = fieldError.getDefaultMessage();
                String field = fieldError.getField();
                errorMessage.append(String.format("%s %s; ", field, defaultMessage));
            }
            exceptionMessage = errorMessage.toString();
        }
        return AjaxResult.failure(exceptionMessage);
    }
}
