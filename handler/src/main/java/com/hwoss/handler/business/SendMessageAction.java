package com.hwoss.handler.business;

import com.common.domain.TaskInfo;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.hwoss.handler.handler.HandlerChannelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMessageAction implements BusinessProcess<TaskInfo> {
    @Autowired
    private HandlerChannelHolder handlerHolder;

    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        handlerHolder.route(taskInfo.getSendChannel()).doHandler(taskInfo);
    }
}
