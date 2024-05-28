package com.hwoss.web.exception;


import com.common.enums.RespStatusEnum;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = "com.hwoss.web.controller")
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public BasicResultVo<String> exception(Exception e) {
        String stackTraceAsString = Throwables.getStackTraceAsString(e);
        log.error(stackTraceAsString);
        return BasicResultVo.fail(RespStatusEnum.ERROR_500, stackTraceAsString);
    }


    @ExceptionHandler({CommonException.class})   //捕获异常
    @ResponseStatus(HttpStatus.OK)
    public BasicResultVo<RespStatusEnum> commonResponse(CommonException ce) {
        log.error(Throwables.getStackTraceAsString(ce));
        return new BasicResultVo(ce.getCode(), ce.getMessage(), ce.getRespStatusEnum());
    }

}
