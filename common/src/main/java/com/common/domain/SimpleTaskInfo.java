package com.common.domain;


import lombok.*;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 调用发送接口成功后返回对应的信息，用于查看下发情况,封装下发信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SimpleTaskInfo {
    /**
     * 作为发送信息这个动作的Id，用于后面可以进行追踪
     */
    private String bizId;

    /**
     * 当前任务的Id
     */
    private String businessId;

    /**
     * 任务携带的消息Id
     */
    private String messageId;
}
