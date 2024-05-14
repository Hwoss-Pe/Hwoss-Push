package com.hwoss.suport.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Value("${hwoss.rabbitmq.routing.send.key}")
    private String send_key;

    @Value("${hwoss.rabbitmq.routing.recall.key}")
    private String recall_key;

    @Value("${hwoss.rabbitmq.send.queues}")
    private String send_queue;

    @Value("${hwoss.rabbitmq.recall.queues}")
    private String recall_queue;
    @Value("${hwoss.rabbitmq.exchange.name}")
    private String exchangeName;


    @Bean(name = "sendQueue")
    public Queue sendQueue() {
        return new Queue(send_queue);
    }

    @Bean(name = "recallQueue")
    public Queue recallQueue() {
        return new Queue(recall_queue);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding bind(@Qualifier("sendQueue") Queue Queue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(Queue1).to(topicExchange).with(send_key);
    }

    @Bean
    public Binding binds(@Qualifier("recallQueue") Queue Queue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(Queue1).to(topicExchange).with(recall_key);
    }
}
