package com.hwoss.handler.deduplication.builder;

import cn.hutool.core.date.DateUtil;
import com.common.domain.TaskInfo;
import com.common.enums.AnchorState;
import com.common.enums.DeduplicationType;
import com.hwoss.handler.deduplication.DeduplicationParam;

import java.util.Date;
import java.util.Objects;

public class FrequencyDeduplicationBuilder extends AbstractDeduplicationBuilder {
    public FrequencyDeduplicationBuilder() {
        deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }

    @Override
    public DeduplicationParam buildDeduplicationParam(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setAnchorState(AnchorState.RULE_DEDUPLICATION);
        //计算当前发送时间，并且算出当天剩余时间化作秒作为redis里该信息过期时间
        deduplicationParam.setDeduplicationTime((DateUtil.endOfDay(new Date()).getTime() - DateUtil.current()) / 1000);
        return deduplicationParam;
    }
}
