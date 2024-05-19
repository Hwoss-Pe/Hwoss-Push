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
//        if (ChannelType.MINI_PROGRAM.getCode().equals(taskInfo.getSendChannel())
//                || ChannelType.OFFICIAL_ACCOUNT.getCode().equals(taskInfo.getSendChannel())
//                || ChannelType.ALIPAY_MINI_PROGRAM.getCode().equals(taskInfo.getSendChannel())) {
//            TaskInfo taskClone = ObjectUtil.cloneByStream(taskInfo);
//            for (String receiver : taskInfo.getReceiver()) {
//                taskClone.setReceiver(Sets.newHashSet(receiver));
//                handlerHolder.route(taskInfo.getSendChannel()).doHandler(taskClone);
//            }
//            return;
//        }
        handlerHolder.route(taskInfo.getSendChannel()).doHandler(taskInfo);
    }
}
