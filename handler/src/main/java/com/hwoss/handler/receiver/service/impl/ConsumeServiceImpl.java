package com.hwoss.handler.receiver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;
import com.hwoss.handler.handler.HandlerHolder;
import com.hwoss.handler.pending.HandlerChannelHolder;
import com.hwoss.handler.pending.Task;
import com.hwoss.handler.pending.TaskPendingHolder;
import com.hwoss.handler.receiver.service.ConsumeService;
import com.hwoss.handler.utils.GroupIdMappingUtils;
import com.hwoss.suport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ConsumeServiceImpl implements ConsumeService {

    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";

    @Autowired
    private ApplicationContext context;

    /**
     * 存储对应业务groupId和具体线程池的映射
     */
    @Autowired
    private TaskPendingHolder taskPendingHolder;

    @Autowired
    private LogUtils logUtils;


    /**
     * 存储发送渠道和对应的处理器
     */
    @Autowired
    private HandlerChannelHolder handlerChannelHolder;


    @Override
    public void consumeSend(List<TaskInfo> taskInfoLists) {
//            先判断当前消息的发送类型是什么
        String groupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));
        for (TaskInfo taskInfo : taskInfoLists) {
//            相对来说简单粗暴，去获取对应的任务类Bean，获取时创建，设置任务后交给对应的线程池去执行
            Task task = context.getBean(Task.class).setTaskInfo(taskInfo);
            taskPendingHolder.route(groupId).execute(task);
        }

    }

    @Override
    public void consumeRecall(RecallTaskInfo recallTaskInfo) {
//        由于撤回消息目前业务线只有一个内容，直接执行就行，而上面的要放到线程池去走一遍消费的责任链，因为撤回频率低
        handlerChannelHolder.route(recallTaskInfo.getSendChannel()).handleRecall(recallTaskInfo);
    }
}
