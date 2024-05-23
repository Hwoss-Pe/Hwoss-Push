package com.hwoss.handler.receiver.kafka;

import cn.hutool.core.text.StrPool;
import com.hwoss.handler.utils.GroupIdMappingUtils;
import com.hwoss.suport.Contents.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author Hwoss
 * @date 2024/05/23
 * 把消费者进行一些初始化配置
 */
@Service
@ConditionalOnProperty(name = "hwoss.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
@Slf4j
public class ReceiverStart {
    /**
     * receiver的消费方法常量
     */
    private static final String RECEIVER_METHOD_NAME = "Receiver.consumer";
    /**
     * 获取得到所有的groupId
     */
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroupId();//渠道加消息类型
    /**
     * 下标(用于迭代groupIds位置)
     */
    private static Integer index = 0;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ConsumerFactory consumerFactory;

    /**
     * @return {@link KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer }
     * \实例创建的时候对于每个Receiver方法里面consume方法上注解进行默认的groupId的值进行初始化
     */
    @Bean
    public static KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer groupIdEnhancer() {
        return (attribute, element) -> {
            if (element instanceof Method) {
//                就是类名.方法名
                String groupId = ((Method) element).getDeclaringClass().getSimpleName() + StrPool.DOT + ((Method) element).getName();
                if (groupId.equals(RECEIVER_METHOD_NAME)) {
                    attribute.put("groupId", groupIds.get(index++));
                }
            }

            return attribute;
        };
    }

    /**
     * 把对应的类设置成多例模式
     */
    @PostConstruct
    public void init() {
        for (int i = 0; i < groupIds.size(); i++) {
//        由于设置成多例模式，获取一个bean的时候就会创建一个实例
            context.getBean(Receiver.class);
        }
    }

    //配置针对请求头数据进行过滤（可不配置）
    public ConcurrentKafkaListenerContainerFactory filter(@Value("hwoss.business.tagId.key") String tagIdKey, @Value("hwoss.business.tagId.value") String tagIdValue) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        factory.setAckDiscarded(true);
        factory.setRecordFilterStrategy(
                record -> {
                    if (Optional.ofNullable(record.value()).isPresent()) {
                        for (Header header : record.headers()) {
                            if (header.key().equals(tagIdKey) && new String(header.value()).equals(tagIdValue)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
        );
        return factory;
    }
}

