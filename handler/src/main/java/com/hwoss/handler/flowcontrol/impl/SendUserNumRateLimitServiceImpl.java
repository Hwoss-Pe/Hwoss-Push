package com.hwoss.handler.flowcontrol.impl;

import com.common.domain.TaskInfo;
import com.common.enums.RateLimitStrategy;
import com.google.common.util.concurrent.RateLimiter;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.limit.LimitService;
import com.hwoss.handler.deduplication.service.AbstractDeduplicationService;
import com.hwoss.handler.flowcontrol.FlowControlParam;
import com.hwoss.handler.flowcontrol.FlowControlService;
import com.hwoss.handler.flowcontrol.LocalRateLimit;

import java.util.Set;


/**
 * @author Hwoss
 * @date 2024/05/20
 * 这里用的是接受数进行限流，那么会根据接受者进行限流
 * 对于acquire()如果限流10个请求/s,那么对于acquire(5)会阻塞2秒
 */
@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.SEND_USER_NUM_RATE_LIMIT)
public class SendUserNumRateLimitServiceImpl implements FlowControlService {
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(taskInfo.getReceivers().size());
    }
}
