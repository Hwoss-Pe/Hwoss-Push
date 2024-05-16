package com.hwoss.suport.utils;

import com.dtp.core.DtpRegistry;
import com.dtp.core.thread.DtpExecutor;
import com.hwoss.suport.config.ThreadPoolExecutorShutdownDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreadPoolUtils {
    private static final String SOURCE_NAME = "hwoss";

    @Autowired
    private ThreadPoolExecutorShutdownDefinition shutdownDefinition;

    /**
     * 把当前的线程池注册到动态线程池里面
     * 并且把线程池交给Spring管理进行一个优雅关闭
     */
    public void register(DtpExecutor dtpExecutor) {
        DtpRegistry.register(dtpExecutor, SOURCE_NAME);
        shutdownDefinition.registryExecutor(dtpExecutor);
    }
}
