package com.hwoss.handler.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;
import com.hwoss.handler.handler.HandlerHolder;
import com.hwoss.handler.pending.TaskPendingHolder;
import com.hwoss.handler.receiver.service.ConsumeService;
import com.hwoss.handler.utils.GroupIdMappingUtils;
import com.mysql.jdbc.log.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class ConsumeServiceImpl implements ConsumeService {
    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";
//
//    @Autowired
//    private ApplicationContext context;
//
//    @Autowired
//    private TaskPendingHolder taskPendingHolder;
//
//    @Autowired
//    private LogUtils logUtils;
//    @Autowired
//    private HandlerHolder handlerHolder;

    @Override
    public void consumeSend(List<TaskInfo> taskInfoLists) {
        GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
        for (TaskInfo taskInfo : taskInfoLists) {

        }
    }

    @Override
    public void consumeRecall(RecallTaskInfo recallTaskInfo) {

    }
}
