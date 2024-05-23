package com.hwoss.handler.receiver.kafka;


import com.hwoss.suport.Contents.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)  //设置成多例模式
@ConditionalOnProperty(name = "hwoss.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class Receiver {
}
