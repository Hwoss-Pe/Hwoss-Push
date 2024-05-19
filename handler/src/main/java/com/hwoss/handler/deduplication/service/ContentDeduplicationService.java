package com.hwoss.handler.deduplication.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.common.domain.TaskInfo;
import com.common.enums.DeduplicationType;
import com.hwoss.handler.deduplication.limit.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ContentDeduplicationService extends AbstractDeduplicationService {

    @Autowired
    public ContentDeduplicationService(@Qualifier("SlideWindowLimitService") LimitService limitService) {
        this.limitService = limitService;
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    /**
     * 内容去重 构建key
     * <p>
     * key: md5(templateId + receiver + content)
     * <p>
     * 相同的内容相同的模板短时间内发给同一个人
     * 注意这里的内容是具体内容
     *
     * @param taskInfo
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return DigestUtil.md5Hex(taskInfo.getMessageTemplateId() + receiver
                                         + JSON.toJSONString(taskInfo.getContentModel()));
    }
}
