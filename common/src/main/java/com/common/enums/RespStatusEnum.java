package com.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Hwoss
 * @date 2024/05/09
 * 返回信息的状态
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespStatusEnum {
    //枚举放在前面

    /**
     * 错误的编码
     */
    ERROR_500("500", "服务器发生错误"),
    ERROR_400("400", "请求发生错误"),


    /**
     * TODO 这里进行了更改他的状态响应码，如果后续数据库配对不上可以返回
     */
    SUCCESS("1", "调用成功"),
    FAIL("0", "调用失败"),

    /**
     * 客户端
     */
    CLIENT_BAD_PARAMETER("C01", "客户端的参数配置错误"),
    TEMPLATE_NOT_FOUND("C02", "模板找不到对应的数据"),
    TOO_MANY_REQUEST("C03", "单次请求接收者大于100"),
    NO_LOGIN("C04", "请先登录"),
    LOGIN("C05", "测试环境，无需登录"),

    /**
     * 服务端
     */
    SERVICE_ERROR("S01", "服务器出现异常"),
    RESOURCE_NOT_FOUND("S02", "服务资源找不到"),

    /**
     * 对于对于责任链通道的校验需要
     */
    CONTEXT_IS_NULL("P01", "流程上下文为空"),
    BUSINESS_CODE_IS_NULL("P02", "业务代码为空"),
    PROCESS_TEMPLATE_IS_NULL("P03", "流程模板为空"),
    PROCESS_LIST_IS_NULL("P04", "业务流程处理器为空"),

    ;
    /**
     * 返回状态
     */
    private final String code;
    /**
     * 返回的信息
     */
    private final String msg;


}
