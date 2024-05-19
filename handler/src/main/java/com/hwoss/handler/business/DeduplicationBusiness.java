package com.hwoss.handler.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import com.common.constant.CommonConstant;
import com.common.domain.TaskInfo;
import com.common.enums.DeduplicationType;
import com.common.enums.EnumsUtils;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.hwoss.handler.deduplication.DeduplicationHolder;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.suport.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DeduplicationBusiness implements BusinessProcess<TaskInfo> {


    public static final String DEDUPLICATION_RULE_KEY = "deduplicationRule";

    @Autowired
    private ConfigService config;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    @Override
    public void process(ProcessContext<TaskInfo> context) {
        TaskInfo taskInfo = context.getProcessModel();
        // 配置样例{"deduplication_10":{"num":1,"time":300},"deduplication_20":{"num":5}}
        String deduplicationConfig = config.getProperty(DEDUPLICATION_RULE_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        //获取所有所有的去重逻辑
        List<Integer> deduplicationList = EnumsUtils.getCodeList(DeduplicationType.class);
        for (Integer deduplicationType : deduplicationList) {
//            只需要传入对应的配置,获取对应配置词条进行赋值，和taskInfo就可以得到deduplicationParam
            DeduplicationParam deduplicationParam = deduplicationHolder.selectBuilder(deduplicationType).build(deduplicationConfig, taskInfo);
            if (Objects.nonNull(deduplicationParam)) {
//                然后独一
                deduplicationHolder.selectService(deduplicationType).deduplication(deduplicationParam);
            }
        }

        if (CollUtil.isEmpty(taskInfo.getReceivers())) {
            context.setIsBreak(true);
        }
    }
}
