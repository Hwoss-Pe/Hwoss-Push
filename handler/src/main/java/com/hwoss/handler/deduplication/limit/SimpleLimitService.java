package com.hwoss.handler.deduplication.limit;

import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.service.AbstractDeduplicationService;
import com.hwoss.suport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class SimpleLimitService extends AbstractLimitService {
    private static final String LIMIT_TAG = "SP_";

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Set<String> filter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {
        return null;
    }
}
