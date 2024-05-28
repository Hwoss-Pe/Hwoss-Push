package com.hwoss.web.exception;


import com.common.enums.RespStatusEnum;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private String code = RespStatusEnum.ERROR_400.getCode();
    private RespStatusEnum respStatusEnum = null;

    public CommonException(String message) {
        super(message);
    }

    public CommonException(RespStatusEnum respStatusEnum) {
        super(respStatusEnum.getMsg());
        this.code = respStatusEnum.getCode();
        this.respStatusEnum = respStatusEnum;
    }

    public CommonException(String code, String message) {
        super(message);
        this.code = code;
    }

    public CommonException(String code, String message, RespStatusEnum respStatusEnum) {
        super(message);
        this.code = code;
        this.respStatusEnum = respStatusEnum;
    }

}
