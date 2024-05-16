package com.hwoss.suport.utils;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.CustomLogListener;
import com.alibaba.fastjson.JSON;
import com.common.domain.LogParam;
import com.hwoss.suport.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogUtils extends CustomLogListener {

    @Autowired
    private SendMqService mqService;

    @Value("${hwoss.business.log.topic.name}")
    private String topicName;

    /**
     * @param logDTO
     * @throws Exception 原理是通过方法切面的日志
     *                   在对应的类上加入@OperationLog 所产生
     */
    @Override
    public void createLog(LogDTO logDTO) throws Exception {
        log.info(JSON.toJSONString(logDTO));
    }

    /**
     * 记录当前对象信息
     */
    public void print(LogParam logParam) {
        logParam.setTimestamp(System.currentTimeMillis());
        log.info(JSON.toJSONString(logParam));
    }
}
