package com.hwoss.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class KafkaController {
    //绑定topic
    private final static String topic_name = "hw_data";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/add")
    public String add(String msg) {
        kafkaTemplate.send(topic_name, "key", msg);
        return String.format("消息%s发送成功!", msg);
    }
}