package com.hwoss.suport.utils;

import com.hwoss.suport.config.ThreadPoolExecutorShutdownDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class ThreadPoolUtils {
    private static final String SOURCE_NAME = "hwoss";

    @Autowired
    private ThreadPoolExecutorShutdownDefinition shutdownDefinition;

    /**
     * 把当前的线程池注册到动态线程池里面
     * 并且把线程池交给Spring管理进行一个优雅关闭
     */
//    public void register(DtpExecutor dtpExecutor) {
//
//    }
}
