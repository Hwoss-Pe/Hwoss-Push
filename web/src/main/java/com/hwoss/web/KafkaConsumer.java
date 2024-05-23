package com.hwoss.web;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    //设置监听topic
    @KafkaListener(topics = "hw_data", groupId = "111")
    public void listen(@Header(KafkaHeaders.GROUP_ID) String topic, String record) {
        System.out.printf("收到消息%s%n", record);
        System.out.println(topic);
    }
}