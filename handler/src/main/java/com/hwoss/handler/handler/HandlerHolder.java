package com.hwoss.handler.handler;

import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;

public interface HandlerHolder {

    void handleSend(TaskInfo taskInfo);


    void handleRecall(RecallTaskInfo recallTaskInfo);
}
