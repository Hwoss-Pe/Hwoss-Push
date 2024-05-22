package com.hwoss.suport.config;

import cn.hutool.core.thread.ExecutorBuilder;
import com.common.constant.ThreadPoolConstant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Hwoss
 * @date 2024/05/22
 * <p>
 * *业务：实现pending队列的单线程池
 * *配置：核心线程可以被回收，当线程池无被引用且无核心线程数，应当被回收,区别于另一个线程池配置
 */
public class SupportThreadPoolConfig {
    public SupportThreadPoolConfig() {

    }

    /**
     * @return {@link ExecutorService }
     * 这个线程池就是为了有个队列执行的时候保证顺序一定，还能作为一个缓存使用
     */
    public static ExecutorService getPendingSingleThreadPool() {
        return ExecutorBuilder.create()
                .setAllowCoreThreadTimeOut(true)
                .setCorePoolSize(ThreadPoolConstant.SINGLE_CORE_POOL_SIZE)
                .setKeepAliveTime(ThreadPoolConstant.SMALL_KEEP_LIVE_TIME, TimeUnit.SECONDS)
                .setWorkQueue(new LinkedBlockingQueue<>(ThreadPoolConstant.BIG_QUEUE_SIZE))
                .setMaxPoolSize(ThreadPoolConstant.SINGLE_MAX_POOL_SIZE)
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();
    }
}
