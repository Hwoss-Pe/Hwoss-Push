package com.hwoss.suport.utils;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.CustomLogListener;
import com.alibaba.fastjson.JSON;
import com.common.domain.AnchorInfo;
import com.common.domain.LogParam;
import com.google.common.base.Throwables;
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
     * 记录当前对象信息,并且把日志发送到mq里面
     */
    public void print(LogParam logParam) {
        logParam.setTimestamp(System.currentTimeMillis());
        log.info(JSON.toJSONString(logParam));


    }

    public void print(AnchorInfo anchorInfo) {
        anchorInfo.setLogTimestamp(System.currentTimeMillis());
        String message = JSON.toJSONString(anchorInfo);
        log.info(message);
//        这里要发送日志到mq里面，后面可以进行收集，不过key得改队列，因此这里的东西建议在kafka实现后弄
//        try {
//            mqService.send(topicName, message);
//        } catch (Exception e) {
//            log.error("LogUtils#print send mq fail! e:{},params:{}", Throwables.getStackTraceAsString(e)
//                    , JSON.toJSONString(anchorInfo));
//        }
    }

    /**
     * 记录当前对象信息和打点信息
     */
    public void print(LogParam logParam, AnchorInfo anchorInfo) {
        print(anchorInfo);
        print(logParam);
    }
}
