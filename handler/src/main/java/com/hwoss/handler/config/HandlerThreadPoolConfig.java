package com.hwoss.handler.config;

import com.common.constant.ThreadPoolConstant;
import com.dtp.common.em.QueueTypeEnum;
import com.dtp.common.em.RejectedTypeEnum;
import com.dtp.core.thread.DtpExecutor;
import com.dtp.core.thread.ThreadPoolBuilder;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HandlerThreadPoolConfig {
    private static final String PRE_FIX = "hwoss.";

    private HandlerThreadPoolConfig() {

    }

    /**
     * 业务：处理某个渠道的某种类型消息的线程池
     * 配置：不丢弃消息，核心线程数不会随着keepAliveTime而减少(不会被回收)
     *
     * @return
     */
    public static DtpExecutor getExecutor(String groupId) {
//        这里设置最大线程和核心线程一样多，如果多会触发拒绝策略交给当前线程处理，并且队列是不公平的
//        可以进行线程插队的操作,并且不丢弃消息，核心线程数不会随着keepAliveTime而减少
        return ThreadPoolBuilder.newBuilder()
                .threadPoolName(PRE_FIX + groupId)
                .corePoolSize(ThreadPoolConstant.COMMON_CORE_POOL_SIZE)
                .keepAliveTime(ThreadPoolConstant.COMMON_KEEP_LIVE_TIME)
                .maximumPoolSize(ThreadPoolConstant.COMMON_MAX_POOL_SIZE)
                .timeUnit(TimeUnit.SECONDS)
                .allowCoreThreadTimeOut(false)
                .workQueue(QueueTypeEnum.LINKED_BLOCKING_QUEUE.getName(), ThreadPoolConstant.COMMON_QUEUE_SIZE, false)
                .rejectedExecutionHandler(RejectedTypeEnum.CALLER_RUNS_POLICY.getName())
                .buildDynamic();
    }
}
