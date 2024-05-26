package com.hwoss.pending;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Hwoss
 * @date 2024/05/24
 * 定义泛型延迟队列，用线程池进行处理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Pending<T> {
    //线程池实例
    private ExecutorService executorService;
    //    多线程用阻塞队列安全
    private BlockingQueue<T> queue;
    //    触发的阈值
    private Integer numThreshold;
    //    最晚触发的时间
    private Long timeThreshold;
}
