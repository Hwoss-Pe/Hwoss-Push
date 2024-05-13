package com.hwoss.service.impl.business;

import cn.hutool.core.text.StrPool;
import com.common.constant.FuncConstant;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.hwoss.service.impl.domain.SendTaskModel;
import com.hwoss.service.api.pojo.MessageParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Hwoss
 * @date 2024/05/11
 * 做的前置参数检查
 */
@Service
@Slf4j
public class SendPreCheckBusiness implements BusinessProcess<SendTaskModel> {
    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        //判断模板和消息
        if (Objects.isNull(messageTemplateId) || messageParamList.isEmpty()) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.CLIENT_BAD_PARAMETER, "模板Id获得消息内容为空"));
            return;
        }
        //过滤接受者null用stream流去过滤
        List<MessageParam> resultMessageParamList = messageParamList.stream()
                .filter(messageParam -> !Objects.isNull(messageParam.getReceiver()))
                .collect(Collectors.toList());

        if (resultMessageParamList.isEmpty()) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.CLIENT_BAD_PARAMETER, "接收者为空"));
            return;
        }

        //由于渠道商限制，过滤单次消息内下发不能超过100条，策略若有一个不符合则整个拒绝保持事务性
        if (resultMessageParamList.stream().anyMatch(messageParam -> messageParam.getReceiver().split(StrPool.COMMA).length > FuncConstant.BATCH_RECEIVER_SIZE)) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.TOO_MANY_REQUEST, RespStatusEnum.TOO_MANY_REQUEST.getMsg()));
            return;
        }
        sendTaskModel.setMessageParamList(resultMessageParamList);
    }
}
