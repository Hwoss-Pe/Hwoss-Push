package com.common.domain;

import com.common.dto.model.ContentModel;
import com.common.pipeline.ProcessModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 把所有能想到的变量信息全部塞在这里，然后通过不同模型设置对应数据就行
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfo implements Serializable, ProcessModel {
    /**
     * 消息发送的Id
     */
    private String bizId;

    /**
     * 消息Id
     */
    private String messageId;

    /**
     * 业务Id
     */
    private Long businessId;

    /**
     * 模板Id
     */
    private Long messageTemplateId;

    /**
     * 接受者
     */
    private Set<String> receivers;

    /**
     * 发送的对象类型如手机号或者qq
     */
    private Integer idType;

    /**
     * 发送渠道
     */
    private Integer sendChannel;
    /**
     * 消息类型
     */
    private Integer msgType;


    /**
     * 模板类型
     */
    private Integer templateType;
    /**
     * 屏蔽类型
     */
    private Integer shieldType;

    /**
     * 发送文案模型
     * message_template表存储的content是JSON(所有内容都会塞进去)
     * 不同的渠道要发送的内容不一样(比如发push会有img，而短信没有)
     * 作用理解待定
     */
    private ContentModel contentModel;

    /**
     * 发送账号（邮件下可有多个发送账号、短信可有多个发送账号..）
     */
    private Integer sendAccount;
}
