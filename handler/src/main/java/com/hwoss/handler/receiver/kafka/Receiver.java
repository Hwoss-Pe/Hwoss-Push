package com.hwoss.handler.receiver.kafka;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;
import com.hwoss.handler.receiver.service.ConsumeService;
import com.hwoss.handler.utils.GroupIdMappingUtils;
import com.hwoss.suport.Contents.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.swing.table.TableStringConverter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)  //设置成多例模式
@ConditionalOnProperty(name = "hwoss.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class Receiver {

    @Value("hwoss.business.topic.name")
    String topicSendName;

    @Value("hwoss.business.recall.topic.name")
    String topicRecallName;

    @Value("hwoss.business.recall.group.name")
    String recallGroupName;

    @Autowired
    private ConsumeService consumeService;

    @KafkaListener(topics = "${hwoss.business.topic.name}", containerFactory = "filter")
//    消费的类型key做泛型，value做String，提高可用性，可以做转换检查，后面用Optional转String
    public void consumer(ConsumerRecord<?, String> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId) {
        Optional<String> value = Optional.ofNullable(consumerRecord.value());
        if (value.isPresent()) {

            List<TaskInfo> taskInfoList = JSON.parseArray(value.get(), TaskInfo.class);
            String groupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoList.iterator()));
//        获取当前消费者的消费者组id,通过对比当前消费者id和传入的任务的类型是否一样进行消费
            if (groupId.equals(topicGroupId)) {
                consumeService.consumeSend(taskInfoList);
            }
        }
    }

    /**
     * 撤回消息
     *
     * @param consumerRecord
     */
    @KafkaListener(topics = "${hwoss.business.recall.topic.name}", groupId = "${hwoss.business.recall.group.name}", containerFactory = "filter")
    public void recall(ConsumerRecord<?, String> consumerRecord) {
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {
            RecallTaskInfo recallTaskInfo = JSON.parseObject(kafkaMessage.get(), RecallTaskInfo.class);
            consumeService.consumeRecall(recallTaskInfo);
        }
    }
}



