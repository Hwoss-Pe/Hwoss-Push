package com.hwoss.handler.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.constant.CommonConstant;
import com.common.domain.AnchorInfo;
import com.common.domain.TaskInfo;
import com.common.enums.AnchorState;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.hwoss.suport.service.ConfigService;
import com.hwoss.suport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 丢弃消息
 * 一般将需要丢弃的模板id写在分布式配置中心
 */
@Service
public class DiscardBusiness implements BusinessProcess<TaskInfo> {

    private static final String DISCARD_MESSAGE_KEY = "discard";

    @Autowired
    private ConfigService configService;
    @Autowired
    private LogUtils logUtils;

//    通过messageId对模板进行阻断发送
@Override
public void process(ProcessContext<TaskInfo> context) {
    TaskInfo taskInfo = context.getProcessModel();
    JSONArray array = JSON.parseArray(configService.getProperty(DISCARD_MESSAGE_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY));
//       如果模板处于隔绝状态就直接在下发的时候阻断
    if (array.contains(taskInfo.getMessageTemplateId().toString())) {
        logUtils.print(AnchorInfo.builder()
                               .businessId(taskInfo.getBusinessId())
                               .messageId(taskInfo.getMessageId())
                               .bizId(taskInfo.getBizId())
                               .ids(taskInfo.getReceivers())
                               .state(AnchorState.DISCARD.getCode())
                               .build());
        context.setIsBreak(true);
    }
}
}
