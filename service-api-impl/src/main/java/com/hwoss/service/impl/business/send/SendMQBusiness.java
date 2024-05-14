package com.hwoss.service.impl.business.send;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.domain.TaskInfo;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import com.hwoss.suport.mq.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hwoss.service.impl.domain.SendTaskModel;

import java.util.List;

@Service
@Slf4j
public class SendMQBusiness implements BusinessProcess<SendTaskModel> {

    @Autowired
    private MqService sendMqService;
    @Value("${hwoss.rabbitmq.routing.send.key}")
    private String key;
//    @Value("${hwoss.rabbitmq.exchange.name}")
//    private String exchangeName;

    @Value("${hwoss.business.tagId.value}")
    private String tagId;

    @Value("${hwoss.mq.pipeline}")
    private String mqPipeline;

    /**
     * @param context 这把信息序列化后传入到mq里面
     */
    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        List<TaskInfo> taskInfoList = sendTaskModel.getTaskInfoList();
        //序列化的方式采用的是记录对象的类，到时还原的时候更加准确
        try {
            String message = JSON.toJSONString(taskInfoList, SerializerFeature.WriteClassName);
            sendMqService.send(key, message, tagId);
        } catch (Exception e) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("send {} fail! e:{},params:{}", mqPipeline, Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(CollUtil.getFirst(taskInfoList.listIterator())));
        }
    }
}
