package com.hwoss.handler.receiver.rabbit;

import com.alibaba.fastjson.JSON;
import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;
import com.hwoss.handler.receiver.service.ConsumeService;
import com.hwoss.suport.Contents.MessageQueuePipeline;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.StringUtil;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.RABBIT_MQ)
public class RabbitMqReceiver {

    private static final String MSG_TYPE_SEND = "send";
    private static final String MSG_TYPE_RECALL = "recall";

    @Autowired
    private ConsumeService consumeService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${hwoss.rabbitmq.message.queues}", durable = "true"),
            exchange = @Exchange(value = "${hwoss.rabbitmq.exchange.name}", type = ExchangeTypes.TOPIC),
            key = "${hwoss.rabbitmq.routing.message.key}"
    ))
    public void onMessage(Message message) {
        String messageType = message.getMessageProperties().getHeader("messageType");
        byte[] body = message.getBody();
        String messageContent = new String(body);
        if (StringUtils.isBlank(messageContent)) {
            return;
        }
        if (messageType.equals(MSG_TYPE_SEND)) {
//           处理发送消息逻辑
            List<TaskInfo> taskInfoList = JSON.parseArray(messageContent, TaskInfo.class);
            consumeService.consumeSend(taskInfoList);
        } else if (messageType.equals(MSG_TYPE_RECALL)) {
//            处理撤回消息的逻辑
            RecallTaskInfo recallTaskInfo = JSON.parseObject(messageContent, RecallTaskInfo.class);
            consumeService.consumeRecall(recallTaskInfo);
        }
    }
}
