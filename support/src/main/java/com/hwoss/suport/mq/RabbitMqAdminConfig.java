package com.hwoss.suport.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqAdminConfig {
    @Value("${hwoss.rabbitmq.topic.name}")
    private String topic;

    @Value("${hwoss.rabbitmq.queues}")
    private String queues;

    @Value("${hwoss.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${hwoss.rabbitmq.routing.key}")
    private String key;

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
        return BindingBuilder.bind(Queue1).to(topicExchange).with(key);
    }
}
