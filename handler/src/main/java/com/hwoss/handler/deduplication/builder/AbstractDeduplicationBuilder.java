package com.hwoss.handler.deduplication.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationHolder;
import com.hwoss.handler.deduplication.DeduplicationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;


public abstract class AbstractDeduplicationBuilder implements Builder {

    protected Integer deduplicationType;


    @Autowired
    private DeduplicationHolder deduplicationHolder;


    @PostConstruct
    public void init() {
        deduplicationHolder.putBuilder(deduplicationType, this);
    }


    /**
     * @param key
     * @param duplicationConfig
     * @param taskInfo
     * @return {@link DeduplicationParam }
     * 读取对应配置字符串然后转化成对应去重参数封装
     */
    public DeduplicationParam getParamsFromConfig(Integer key, String duplicationConfig, TaskInfo taskInfo) {
        JSONObject object = JSON.parseObject(duplicationConfig);
        if (Objects.isNull(object)) {
            return null;
        }
        DeduplicationParam deduplicationParam = JSON.parseObject(object.getString(DEDUPLICATION_CONFIG_PRE + key), DeduplicationParam.class);
        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setTaskInfo(taskInfo);
        return deduplicationParam;
    }
}
