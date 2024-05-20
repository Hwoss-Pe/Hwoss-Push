package com.hwoss.handler.flowcontrol;

import com.common.domain.TaskInfo;

public interface FlowControlService {
    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo
     * @param flowControlParam
     * @return 耗费的时间，根据返回的时间可以反馈出当前的限流的情况
     */
    Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam);
}
