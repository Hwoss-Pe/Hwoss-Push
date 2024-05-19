package com.hwoss.handler.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.domain.AnchorInfo;
import com.common.domain.TaskInfo;
import com.common.enums.AnchorState;
import com.common.enums.ShieldType;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.hwoss.suport.utils.LogUtils;
import com.hwoss.suport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Hwoss
 * @date 2024/05/17
 * 分为两种，一种是夜间屏蔽不发送，一种是到次日发送
 */
@Service
public class ShieldBusiness implements BusinessProcess<TaskInfo> {


    private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send";
    private static final long SECONDS_OF_A_DAY = 86400L;

    /**
     * 默认早上6点之前是凌晨
     */
    private static final int NIGHT = 6;

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private LogUtils logUtils;

    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        if (taskInfo.getShieldType().equals(ShieldType.NIGHT_SHIELD.getCode())) {
            logUtils.print(AnchorInfo.builder()
                                   .businessId(taskInfo.getBusinessId())
                                   .bizId(taskInfo.getBizId())
                                   .messageId(taskInfo.getMessageId())
                                   .state(AnchorState.NIGHT_SHIELD.getCode())
                                   .logTimestamp(System.currentTimeMillis())
                                   .ids(taskInfo.getReceivers())
                                   .build());
            return;
        }
        if (LocalDateTime.now().getHour() < NIGHT) {
            if (taskInfo.getShieldType().equals(ShieldType.NIGHT_SHIELD_BUT_NEXT_DAY_SEND.getCode())) {
                redisUtils.lPush(NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY,
                                 JSON.toJSONString(taskInfo, SerializerFeature.WriteClassName)
                        , SECONDS_OF_A_DAY);
                logUtils.print(AnchorInfo.builder().state(AnchorState.NIGHT_SHIELD_NEXT_SEND.getCode()).bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceivers()).build());
            }
            if (ShieldType.NIGHT_SHIELD.getCode().equals(taskInfo.getShieldType())) {
                logUtils.print(AnchorInfo.builder().state(AnchorState.NIGHT_SHIELD.getCode())
                                       .bizId(taskInfo.getBizId()).messageId(taskInfo.getMessageId()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceivers()).build());
            }
        }
        context.setIsBreak(true);
    }
}
