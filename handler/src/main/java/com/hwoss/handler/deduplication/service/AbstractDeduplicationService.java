package com.hwoss.handler.deduplication.service;

import com.common.domain.AnchorInfo;
import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationHolder;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.limit.LimitService;
import com.hwoss.suport.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

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

    /**
     * @param deduplicationParam 去重最后收尾，把limit返回的接受给筛选掉
     */
    @Override
    public void deduplication(DeduplicationParam deduplicationParam) {
        TaskInfo taskInfo = deduplicationParam.getTaskInfo();
        Set<String> filter = limitService.filter(this, taskInfo, deduplicationParam);
        if (!filter.isEmpty()) {
            taskInfo.getReceivers().removeAll(filter);
            logUtils.print(AnchorInfo.builder().
                    bizId(taskInfo.getBizId()).
                    messageId(taskInfo.getMessageId()).
                    businessId(taskInfo.getBusinessId())
                                   .ids(filter).
                            state(deduplicationParam.getAnchorState().getCode())
                                   .build());
        }
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
