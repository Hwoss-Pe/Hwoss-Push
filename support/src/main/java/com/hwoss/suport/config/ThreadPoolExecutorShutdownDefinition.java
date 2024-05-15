package com.hwoss.suport.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Hwoss
 * @date 2024/05/15
 * 监听线程池的关闭，进行优雅处理
 */
@Slf4j
@Component
public class ThreadPoolExecutorShutdownDefinition implements ApplicationListener<ContextClosedEvent> {
    /**
     * 线程中的任务在接收到应用关闭信号量后最多等待多久就强制终止，其实就是给剩余任务预留的时间， 到时间后线程池必须销毁
     */
    private static final long AWAIT_TERMINATION = 20;
    /**
     * awaitTermination的单位
     */
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    //    这里定义线程池的执行相关操作作为一个list再用线程安全包装，可以进行共享访问
    private final List<ExecutorService> POOLS = Collections.synchronizedList(new ArrayList<>(12));

    public void registryExecutor(ExecutorService executor) {
        POOLS.add(executor);
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        log.info("容器关闭前处理线程池优雅关闭开始, 当前要处理的线程池数量为: {} >>>>>>>>>>>>>>>>", POOLS.size());
        if (POOLS.isEmpty()) {
            return;
        }
        for (ExecutorService pool : POOLS) {
//            当监听到关闭时间，对当前线程池关闭
            pool.shutdown();
            try {
                if (!pool.awaitTermination(AWAIT_TERMINATION, TIME_UNIT)) {
//                    关闭超时
                    log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                }
            } catch (InterruptedException e) {
//                为了防止当前调用的线程的阻塞，主动进行打断
                log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                Thread.currentThread().interrupt();
            }
        }
    }
}
