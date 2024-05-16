package com.hwoss.suport.mq;

import com.hwoss.suport.Contents.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "hwoss.mq.pipeline", havingValue = MessageQueuePipeline.RABBIT_MQ)
public class RabbitSendMqServiceImpl implements SendMqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${hwoss.rabbitmq.exchange.name}")
    private String exchangeName;


    @Override
    public void send(String key, String jsonValue, String tagId) {
        //交换机，routingKey，值
        rabbitTemplate.convertAndSend(exchangeName, key, jsonValue);
    }

    @Override
    public void send(String topic, String jsonValue) {
        send(exchangeName, jsonValue, "");
    }
}
