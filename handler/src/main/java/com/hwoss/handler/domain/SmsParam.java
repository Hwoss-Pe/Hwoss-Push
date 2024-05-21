package com.hwoss.handler.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author Hwoss
 * @date 2024/05/21
 * /**
 * 发送短信参数
 */
@Data
@Builder
public class SmsParam {

    /**
     * 消息Id
     */
    private Long messageTemplateId;

    /**
     * 需要发送的手机号
     */
    private Set<String> phones;


    /**
     * 发送账号的id（如果短信模板指定了发送账号，则该字段有值）
     * <p>
     * 如果有账号id，那就用账号id 检索
     * 如果没有账号id，就scriptName 检索
     */
    private Integer sendAccountId;

    /**
     * 渠道账号的脚本名标识,比如自动，或者腾讯短信，后者其他对应的短信
     */
    private String scriptName;

    /**
     * 发送文案
     */
    private String content;

}
