package com.hwoss.handler.flowcontrol.impl;

import com.common.domain.TaskInfo;
import com.common.enums.RateLimitStrategy;
import com.google.common.util.concurrent.RateLimiter;
import com.hwoss.handler.flowcontrol.FlowControlParam;
import com.hwoss.handler.flowcontrol.FlowControlService;
import com.hwoss.handler.flowcontrol.LocalRateLimit;

/**
 * @author Hwoss
 * @date 2024/05/20
 * 根据请求进行限流(QPS)
 */
@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.REQUEST_RATE_LIMIT)
public class RequestRateLimitServiceImpl implements FlowControlService {
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(1);
    }
}
