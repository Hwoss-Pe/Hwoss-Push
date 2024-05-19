package com.hwoss.handler.handler;

import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;

/**
 * @author Hwoss
 * @date 2024/05/16
 */

public interface Handler {

    void doHandler(TaskInfo taskInfo);


    void handleRecall(RecallTaskInfo recallTaskInfo);
}
