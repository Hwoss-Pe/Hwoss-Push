package com.hwoss.config;

/**
 * Flink常量信息
 *
 * @author 3y
 */
public class FlinkConstant {
    /**
     * Kafka 配置信息
     */
    public static final String GROUP_ID = "hwossLogGroup";
    public static final String TOPIC_NAME = "hwossTraceLog";
    public static final String BROKER = "120.55.194.151:9092";
    /**
     * redis 配置
     */
    public static final String REDIS_IP = "120.55.194.151";
    public static final String REDIS_PORT = "6379";
    /**
     * Flink流程常量
     */
    public static final String SOURCE_NAME = "hwoss_kafka_source";
    public static final String FUNCTION_NAME = "hwoss_transfer";
    public static final String SINK_NAME = "hwoss_sink";
    public static final String JOB_NAME = "HwossBootStrap";

    private FlinkConstant() {
    }


}
