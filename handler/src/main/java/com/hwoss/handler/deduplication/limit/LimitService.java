package com.hwoss.handler.deduplication.limit;

import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.service.AbstractDeduplicationService;

import java.util.Set;

public interface LimitService {

    Set<String> filter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param);
}
