package com.hwoss.suport.mq;

/**
 * @author Hwoss
 * @date 2024/05/13
 * 让对应的mq实现这个类重写send方法
 */
public interface SendMqService {

    /**
     * @param topic
     * @param jsonValue
     * @param tagId     发送消息，传入jsonValue的具体值
     */
    void send(String topic, String jsonValue, String tagId);

    void send(String topic, String jsonValue);
}
