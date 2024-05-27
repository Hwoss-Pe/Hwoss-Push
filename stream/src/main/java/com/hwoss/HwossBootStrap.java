package com.hwoss;

import com.common.domain.AnchorInfo;
import com.hwoss.business.MapFunction;
import com.hwoss.config.FlinkConstant;
import com.hwoss.sink.HwossSink;
import com.hwoss.utils.MessageQueueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author Hwoss
 * @date 2024/05/27
 * Flink启动类
 */
@Slf4j
public class HwossBootStrap {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//首先获取对应的kafka消费者
        /**
         * 1.获取KafkaConsumer
         */
        KafkaSource<String> kafkaConsumer = MessageQueueUtils.getKafkaConsumer(FlinkConstant.TOPIC_NAME, FlinkConstant.GROUP_ID, FlinkConstant.BROKER);
        DataStreamSource<String> kafkaSource = env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks(), FlinkConstant.SOURCE_NAME);


        /**
         * 2. 数据转换处理
         */
        SingleOutputStreamOperator<AnchorInfo> dataStream = kafkaSource.flatMap(new MapFunction()).name(FlinkConstant.FUNCTION_NAME);

        /**
         * 3. 将实时数据多维度写入Redis
         */
        dataStream.addSink(new HwossSink()).name(FlinkConstant.SINK_NAME);
        try {
            env.execute(FlinkConstant.JOB_NAME);
        } catch (Exception e) {
            log.error("Exception from env.execute");
        }

    }
}
