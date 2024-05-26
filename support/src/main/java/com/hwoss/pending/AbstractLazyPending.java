package com.hwoss.pending;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Throwables;
import com.hwoss.suport.config.SupportThreadPoolConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public abstract class AbstractLazyPending<T> {

    //子类来进行声明初始化类型参数,protected必须设置让子类访问并且赋值
    protected Pending<T> pending;

    //    具体的任务操作
    private List<T> tasks = new ArrayList<T>();


    private long lastHandleTime = System.currentTimeMillis();
    //是否终止线程，对所有线程都是共享访问
    private volatile Boolean stop = false;

//    那么在这个类初始化的时候就可以进行初始化循环消费

    public void init() {
        ExecutorService executorService = SupportThreadPoolConfig.getPendingSingleThreadPool();
        executorService.execute(() -> {
                                    while (true) {
//                 获取参数的队列元素，如果为空则进行等待,非空就加入后任务列表
                                        try {
                                            T poll = pending.getQueue().poll(pending.getTimeThreshold(), TimeUnit.MILLISECONDS);
                                            if (Objects.nonNull(poll)) {
                                                tasks.add(poll);
                                            }
                                            // 判断是否停止当前线程，或者任务列表为空也关闭线程池，
                                            if (Boolean.TRUE.equals(stop) && CollUtil.isEmpty(tasks)) {
                                                executorService.shutdown();
                                                break;
                                            }
//                      进行队列数据判断,如果数据没问题就继续等待，当达到阈值后就清空任务列表和时间重置
                                            if (CollUtil.isNotEmpty(tasks) && dataReady()) {
                                                List<T> taskRef = tasks;
                                                tasks = new ArrayList<>();
                                                lastHandleTime = System.currentTimeMillis();
                                                pending.getExecutorService().execute(
                                                        () -> this.handle(taskRef)
                                                );
                                            }

                                        } catch (Exception e) {
                                            log.error("Pending#initConsumePending failed:{}", Throwables.getStackTraceAsString(e));
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
        );
    }

    /**
     * 消费阻塞队列元素时的方法
     *
     * @param t
     */
    public void handle(List<T> t) {
        if (t.isEmpty()) {
            return;
        }
        try {
            doHandle(t);
        } catch (Exception e) {
            log.error("Pending#handle failed:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 1. 数量超限
     * 2. 时间超限
     *
     * @return
     */
    private boolean dataReady() {
        return tasks.size() >= pending.getNumThreshold() ||
                (System.currentTimeMillis() - lastHandleTime >= pending.getTimeThreshold());
    }

    /**
     * 将元素放入阻塞队列中
     *
     * @param t
     */
    public void pending(T t) {
        try {
            pending.getQueue().put(t);
        } catch (InterruptedException e) {
            log.error("Pending#pending error:{}", Throwables.getStackTraceAsString(e));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理阻塞队列的元素 真正方法
     *
     * @param list
     */
    public abstract void doHandle(List<T> list);
}
