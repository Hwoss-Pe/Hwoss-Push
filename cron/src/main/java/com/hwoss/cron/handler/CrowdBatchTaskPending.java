package com.hwoss.cron.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import com.common.constant.FuncConstant;
import com.google.common.base.Throwables;
import com.hwoss.cron.config.CronAsyncThreadPoolConfig;
import com.hwoss.cron.constants.PendingConstant;
import com.hwoss.cron.vo.CrowdInfoVo;
import com.hwoss.pending.AbstractLazyPending;
import com.hwoss.pending.Pending;
import com.hwoss.service.api.enums.BusinessCode;
import com.hwoss.service.api.pojo.BatchRequest;
import com.hwoss.service.api.pojo.MessageParam;
import com.hwoss.service.api.service.SendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.ListAttribute;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Hwoss
 * @date 2024/05/26
 * 延迟队列进行batch发送信息
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CrowdBatchTaskPending extends AbstractLazyPending<CrowdInfoVo> {

    @Autowired
    private SendService sendService;

    public CrowdBatchTaskPending() {
        Pending<CrowdInfoVo> pendingParam = new Pending<>();
        pendingParam.setQueue(new LinkedBlockingQueue(PendingConstant.QUEUE_SIZE))
                .setTimeThreshold(PendingConstant.TIME_THRESHOLD)
                .setNumThreshold(FuncConstant.BATCH_RECEIVER_SIZE)
//                线程池就是普通的
                .setExecutorService(CronAsyncThreadPoolConfig.getConsumePendingThreadPool());
        this.pending = pendingParam;
    }

    @Override
    public void doHandle(List<CrowdInfoVo> list) {
        if (CollUtil.isEmpty(list)) {
            log.error("CrowdBatchTaskPending failed:{}", "传入的任务列表为空");
        }
//            由于batch信息是可以进行多个接受者逗号隔开分发，因此对于这些信息查看有没有共同的接受者一起发，拼在一起
//              k用变量map，看是否有一样的变量名，然后拼接成多个接受一起调用
        Map<Map<String, String>, String> paramMap = new HashMap<>();
        for (CrowdInfoVo crowdInfoVo : list) {
            Map<String, String> variable = crowdInfoVo.getParams();
            String receiver = crowdInfoVo.getReceiver();
            if (Objects.isNull(paramMap.get(variable))) {
                paramMap.put(variable, receiver);
            } else {
                String newReceiver = StringUtils.join(new String[]{
                        paramMap.get(variable), receiver}, StrPool.COMMA);
                paramMap.put(variable, newReceiver);
            }
        }

//        组装MessageParam参数
        List<MessageParam> messageParams = new ArrayList<>();
        for (Map.Entry<Map<String, String>, String> entry : paramMap.entrySet()) {
            MessageParam messageParam = MessageParam.builder().receiver(entry.getValue())
                    .variables(entry.getKey()).build();
            messageParams.add(messageParam);
        }
//   发送
        BatchRequest batchRequest = BatchRequest.builder()
                .code(BusinessCode.SEND.getCode())
                .messageParamList(messageParams)
                .MessageTemplateId(list.get(0).getMessageTemplateId())
                .build();

        sendService.batchSend(batchRequest);
    }


}
