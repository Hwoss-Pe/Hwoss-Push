package com.hwoss.service.impl.serivce;

import com.common.enums.RespStatusEnum;
import com.common.pipeline.ProcessContext;
import com.common.pipeline.ProcessController;
import com.common.vo.BasicResultVo;
import com.hwoss.service.api.pojo.SendRequest;
import com.hwoss.service.api.pojo.SendResponse;
import com.hwoss.service.api.service.RecallService;
import com.hwoss.service.impl.domain.RecallTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RecallServiceImpl implements RecallService {
    @Autowired
    @Qualifier("businessController")
    private ProcessController processController;

    @Override
    public SendResponse recall(SendRequest request) {
        if (Objects.isNull(request)) {
            return new SendResponse(RespStatusEnum.CLIENT_BAD_PARAMETER.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETER.getMsg());
        }
//        思路如果传模板ID就撤回整个模板，没有就撤回对应的messageId
        RecallTaskModel recallTaskModel = RecallTaskModel.builder()
                .messageTemplateId(request.getMessageTemplateId())
                .recallIdList(request.getRecallList())
                .build();
        ProcessContext processContext = ProcessContext.builder()
                .isBreak(false)
                .processModel(recallTaskModel)
                .Code(request.getCode())
                .response(BasicResultVo.success())
                .build();
        ProcessContext process = processController.process(processContext);
        return new SendResponse(processContext.getResponse().getStatus(), processContext.getResponse().getMsg());

    }
}
