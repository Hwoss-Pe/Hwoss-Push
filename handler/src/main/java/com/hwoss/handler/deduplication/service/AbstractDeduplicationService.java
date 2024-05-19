package com.hwoss.handler.deduplication.service;

import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationHolder;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.limit.LimitService;
import com.hwoss.suport.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService {

    protected Integer deduplicationType;

    protected LimitService limitService;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    protected DeduplicationHolder deduplicationHolder;


    @PostConstruct
    public void init() {
        deduplicationHolder.putService(deduplicationType, this);
    }

    @Override
    public void deduplication(DeduplicationParam deduplicationParam) {

    }


    /**
     * 构建去重的Key
     *
     * @param taskInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(TaskInfo taskInfo, String receiver);
}
