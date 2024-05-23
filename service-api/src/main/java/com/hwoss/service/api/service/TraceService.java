package com.hwoss.service.api.service;

import com.hwoss.service.api.pojo.TraceResponse;

/**
 * @author Hwoss
 * @date 2024/05/22
 * 用于链路的查询
 */
public interface TraceService {

    TraceResponse traceByMessageId(String messageId);

}
