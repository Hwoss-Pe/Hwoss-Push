package com.hwoss.service.impl.serivce;


import com.common.domain.SimpleTaskInfo;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.ProcessContext;
import com.common.pipeline.ProcessController;
import com.common.vo.BasicResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hwoss.service.impl.domain.SendTaskModel;
import com.hwoss.service.api.pojo.BatchRequest;
import com.hwoss.service.api.pojo.SendRequest;
import com.hwoss.service.api.pojo.SendResponse;
import com.hwoss.service.api.service.SendService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SendServiceImpl implements SendService {

    /**
     * 这个会在PipeConfig进行注入
     */
    @Autowired
    private ProcessController processController;


    @Override
    public SendResponse send(SendRequest request) {
        if (Objects.isNull(request)) {
            return new SendResponse(RespStatusEnum.CLIENT_BAD_PARAMETER.getCode(),
                                    RespStatusEnum.CLIENT_BAD_PARAMETER.getMsg());
        }
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageParamList(Collections.singletonList(request.getMessageParam()))
                .MessageTemplateId(request.getMessageTemplateId())
                .build();

        ProcessContext context = ProcessContext.builder()
                .Code(request.getCode())
                .isBreak(false)
                .processModel(sendTaskModel)
                .response(BasicResultVo.success())
                .build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg(),
                                (List<SimpleTaskInfo>) process.getResponse().getData());
    }

    @Override
    public SendResponse batchSend(BatchRequest request) {
//        ProcessContext context = ProcessContext.builder()
//                .Code(request.getCode())
//                .isBreak(false)
//                .processModel(sendTaskModel)
//                .response(BasicResultVo.success())
//                .build();
        return null;
    }


}
