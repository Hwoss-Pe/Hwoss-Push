package com.hwoss.suport.mq.rabbitMq;

import com.hwoss.suport.Contents.MessageQueuePipeline;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hwoss
 * @date 2024/05/14
 * 这个类可能后面不需要的时候要进行注释，或者选择性配置
 */
@Configuration
@ConditionalOnProperty(name = "hwoss.mq.pipeline", havingValue = MessageQueuePipeline.RABBIT_MQ)
public class RabbitMqAdminConfig {
//    @Value("${hwoss.rabbitmq.routing.send.key}")
//    private String send_key;
//
//    @Value("${hwoss.rabbitmq.routing.recall.key}")
//    private String recall_key;

    @Value("${hwoss.rabbitmq.routing.message.key}")
    private String message_key;

    @Value("${hwoss.rabbitmq.message.queues}")
    private String message_queue;

    //    @Value("${hwoss.rabbitmq.recall.queues}")
//    private String recall_queue;
    @Value("${hwoss.rabbitmq.exchange.name}")
    private String exchangeName;


//    @Bean(name = "Queue")
//    public Queue sendQueue() {
//        return new Queue(send_queue);
//    }

    @Bean(name = "Queue")
    public Queue MessageQueue() {
        return new Queue(message_queue);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding bind(@Qualifier("Queue") Queue Queue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(Queue1).to(topicExchange).with(message_key);
    }
//    @Bean
//    public Binding binds(@Qualifier("Queue") Queue Queue1, TopicExchange topicExchange) {
//        return BindingBuilder.bind(Queue1).to(topicExchange).with(recall_key);
//    }

//    @Bean
//    public Binding binds(@Qualifier("recallQueue") Queue Queue1, TopicExchange topicExchange) {
//        return BindingBuilder.bind(Queue1).to(topicExchange).with(recall_key);
//    }
}
