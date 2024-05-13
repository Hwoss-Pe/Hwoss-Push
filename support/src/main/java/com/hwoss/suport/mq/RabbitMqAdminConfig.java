package com.hwoss.suport.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hwoss
 * @date 2024/05/14
 * 这个类可能后面不需要的时候要进行注释，或者选择性配置
 */
@Configuration
public class RabbitMqAdminConfig {
    @Value("${hwoss.rabbitmq.routing.key}")
    private String topic;

    @Value("${hwoss.rabbitmq.queues}")
    private String queues;

    @Value("${hwoss.rabbitmq.exchange.name}")
    private String exchangeName;


    @Bean
    public Queue testQueue() {
        return new Queue(queues);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding bind(Queue Queue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(Queue1).to(topicExchange).with(topic);
    }
}
