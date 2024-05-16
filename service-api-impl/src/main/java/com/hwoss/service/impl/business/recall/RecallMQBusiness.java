package com.hwoss.service.impl.business.recall;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.domain.RecallTaskInfo;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import com.hwoss.service.impl.domain.RecallTaskModel;
import com.hwoss.suport.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RecallMQBusiness implements BusinessProcess<RecallTaskModel> {

    @Autowired
    private SendMqService sendMqService;
    @Value("${hwoss.rabbitmq.routing.message.key}")
    private String key;
//    @Value("${hwoss.rabbitmq.exchange.name}")
//    private String exchangeName;

    @Value("${hwoss.business.tagId.value}")
    private String tagId;

    @Value("${hwoss.mq.pipeline}")
    private String mqPipeline;

    @Override
    public void process(ProcessContext<RecallTaskModel> context) {
        RecallTaskModel recallTaskModel = context.getProcessModel();
        RecallTaskInfo recallTaskInfo = recallTaskModel.getRecallTaskInfo();
        //序列化的方式采用的是记录对象的类，到时还原的时候更加准确
        try {
            String message = JSON.toJSONString(recallTaskInfo, SerializerFeature.WriteClassName);
            sendMqService.send(key, message, tagId);
        } catch (Exception e) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("send {} fail! e:{},params:{}", mqPipeline, Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(recallTaskInfo));
        }
    }
}
