package com.hwoss.handler.pending;

import com.dtp.core.thread.DtpExecutor;
import com.hwoss.handler.config.HandlerThreadPoolConfig;
import com.hwoss.handler.utils.GroupIdMappingUtils;
import com.hwoss.suport.utils.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Hwoss
 * @date 2024/05/16
 * 用个map存储消息类型和对应的缓存关系
 */
public class TaskPendingHolder {
    //存储所有的 消费者组
    private static List<String> groupId = GroupIdMappingUtils.getAllGroupId();

    Map<String, ExecutorService> mappings = new HashMap<>(32);
    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    /**
     * 初始化的时候把映射都丢进去
     */
    @PostConstruct
    public void init() {
        for (String id : groupId) {
            DtpExecutor executor = HandlerThreadPoolConfig.getExecutor(id);
            threadPoolUtils.register(executor);
            mappings.put(id, executor);
        }
    }

    /**
     * @param groupId
     * @return {@link ExecutorService }
     * 通过不同业务获取不同的动态线程池
     */
    public ExecutorService route(String groupId) {
        return mappings.get(groupId);
    }
}
