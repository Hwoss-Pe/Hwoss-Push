package service.api.enums;

import lombok.*;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 这个用来对于请求封装的业务类型代码
 */
@ToString
@Getter
public enum BusinessCode {
    SEND("send", "普通发送"),
    RECALL("recall", "撤回信息"),
    ;

    BusinessCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private final String code;

    private final String description;
}
