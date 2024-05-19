package com.hwoss.handler.deduplication.builder;

import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationParam;

public interface Builder {
    String DEDUPLICATION_CONFIG_PRE = "deduplication_";

    /**
     * 根据配置构建去重参数
     *
     * @param deduplication
     * @param taskInfo
     * @return
     */
    DeduplicationParam buildDeduplicationParam(String deduplication, TaskInfo taskInfo);

}
