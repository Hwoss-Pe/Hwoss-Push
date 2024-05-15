package com.hwoss.web;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    //设置监听topic
    @KafkaListener(topics = "hw_data")
    public void listen(String record) {
        System.out.printf("收到消息%s%n", record);
    }
}