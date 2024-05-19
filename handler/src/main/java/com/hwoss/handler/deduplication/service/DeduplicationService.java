package com.hwoss.handler.deduplication.service;

import com.hwoss.handler.deduplication.DeduplicationParam;

public interface DeduplicationService {

    /**
     * @param deduplicationParam 去重
     */
    void deduplication(DeduplicationParam deduplicationParam);
}
