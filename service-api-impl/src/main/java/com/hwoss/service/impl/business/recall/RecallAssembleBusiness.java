package com.hwoss.service.impl.business.recall;

import com.common.constant.CommonConstant;
import com.common.domain.RecallTaskInfo;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import com.hwoss.service.impl.domain.RecallTaskModel;
import com.hwoss.suport.dao.MessageTemplateDao;
import com.hwoss.suport.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferStrategy;
import java.util.Optional;

@Slf4j
@Service
public class RecallAssembleBusiness implements BusinessProcess<RecallTaskModel> {
    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    public void process(ProcessContext<RecallTaskModel> context) {
        RecallTaskModel recallTaskModel = context.getProcessModel();
        Long messageTemplateId = recallTaskModel.getMessageTemplateId();
        try {
            Optional<MessageTemplate> messageTemplate = messageTemplateDao.findById(messageTemplateId);
            if (!messageTemplate.isPresent() || messageTemplate.get().getIsDeleted().equals(CommonConstant.TRUE)) {
                context.setResponse(BasicResultVo.fail(RespStatusEnum.TEMPLATE_NOT_FOUND)).setIsBreak(true);
                return;
            }
            RecallTaskInfo recallTaskInfo = RecallTaskInfo.builder()
                    .sendAccount(messageTemplate.get().getSendAccount())
                    .sendChannel(messageTemplate.get().getSendChannel())
                    .recallIdList(recallTaskModel.getRecallIdList())
                    .build();
            recallTaskModel.setRecallTaskInfo(recallTaskInfo);
        } catch (Exception e) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("assemble recall task fail! templateId:{}, e:{}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }
    }
}
