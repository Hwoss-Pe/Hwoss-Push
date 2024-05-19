package com.hwoss.handler.deduplication.limit;

import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.service.AbstractDeduplicationService;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLimitService implements LimitService {

    /**
     * @param service
     * @param taskInfo
     * @return {@link List }<{@link String }>
     * 思路先去计算所有key，然后再去过滤
     */
    protected List<String> deduplicationAllKey(AbstractDeduplicationService service, TaskInfo taskInfo) {
        List<String> re = new ArrayList<>(taskInfo.getReceivers().size());
        for (String receiver : taskInfo.getReceivers()) {
            String key = service.deduplicationSingleKey(taskInfo, receiver);
            re.add(key);
        }
        return re;
    }


}
