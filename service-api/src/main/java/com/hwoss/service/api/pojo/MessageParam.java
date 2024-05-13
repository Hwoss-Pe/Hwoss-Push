package com.hwoss.service.api.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 发送的消息的封装
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)//这个主要是用来set方法后直接返回当前对象
public class MessageParam {
    /**
     * 作为消息发送的Id，也用于追踪数据，类似ID
     */
    private String bidId;

    /**
     * 如果存在多个接受者用,分开
     */
    private String receiver;

    /**
     * key作为需要替换的变量，value就是对应的传入的替换值，若没有传入空map
     */
    private Map<String, String> variables;

    /**
     * 拓展参数的设置，可选传入
     */
    private Map<String, String> extra;

    public MessageParam(String bidId, String receiver, Map<String, String> variables) {
        this.bidId = bidId;
        this.receiver = receiver;
        this.variables = variables;
    }
}
