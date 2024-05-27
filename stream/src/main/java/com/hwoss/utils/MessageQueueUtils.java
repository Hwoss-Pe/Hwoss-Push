package com.hwoss.utils;


import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

import static java.util.stream.IntStream.builder;

public class MessageQueueUtils {
    private MessageQueueUtils() {
    }


    /**
     * @param topicName
     * @param groupId
     * @param broker    获取kafka消费者
     * @return {@link KafkaSource }<{@link String }>
     */
    public static KafkaSource<String> getKafkaConsumer(String topicName, String groupId, String broker) {
        return KafkaSource.<String>builder()
                .setBootstrapServers(broker)
                .setGroupId(groupId)
                .setTopics(topicName)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

    }
}
