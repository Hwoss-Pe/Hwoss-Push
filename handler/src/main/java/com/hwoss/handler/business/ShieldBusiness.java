package com.hwoss.handler.business;

import com.common.domain.TaskInfo;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.hwoss.suport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    }
}
