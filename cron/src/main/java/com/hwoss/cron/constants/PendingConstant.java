package com.hwoss.cron.constants;


/**
 * @author Hwoss
 * @date 2024/05/24
 * 延迟队列的一些初始化信息
 */
public class PendingConstant {
    /**
     * 阻塞队列大小
     */
    public static final Integer QUEUE_SIZE = 100;
    /**
     * 触发执行的数量阈值
     */
    public static final Integer NUM_THRESHOLD = 100;
    /**
     * batch 触发执行的时间阈值，单位毫秒【必填】
     */
    public static final Long TIME_THRESHOLD = 1000L;

    private PendingConstant() {
    }


}
