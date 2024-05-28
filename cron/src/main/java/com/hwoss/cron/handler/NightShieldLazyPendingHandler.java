package com.hwoss.cron.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.domain.TaskInfo;
import com.google.common.base.Throwables;
import com.hwoss.cron.service.TaskHandler;
import com.hwoss.suport.config.SupportThreadPoolConfig;
import com.hwoss.suport.utils.RedisUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Slf4j
public class NightShieldLazyPendingHandler {
    private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send";


    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${hwoss.business.topic.name}")
    private String topicName;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 处理夜间屏蔽的发送逻辑，由于夜间屏蔽的数据都会存储在redis里面
     * //     启动定时任务每次到点就读取redis的list数据，把他清空读取就行
     */
    @XxlJob("hwossJob")
    public void execute() {
        log.info("NightShieldLazyPendingHandler#execute!");
        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() ->
                                                                     {
                                                                         while (redisUtils.lLen(NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY) > 0) {
                                                                             String taskInfo = redisUtils.lPop(NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY);
                                                                             if (CharSequenceUtil.isNotBlank(taskInfo)) {
                                                                                 try {
                                                                                     kafkaTemplate.send(topicName, JSON.toJSONString(JSON.parseObject(taskInfo, TaskInfo.class)), new SerializerFeature[]{SerializerFeature.WriteClassName});
                                                                                 } catch (Exception e) {
                                                                                     log.error("nightShieldLazyJob send kafka fail! e:{},params:{}", Throwables.getStackTraceAsString(e), taskInfo);
                                                                                 }
                                                                             }
                                                                         }
                                                                     }
        );
    }
}
