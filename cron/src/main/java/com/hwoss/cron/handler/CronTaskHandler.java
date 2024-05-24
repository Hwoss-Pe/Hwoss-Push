package com.hwoss.cron.handler;

import com.dtp.core.thread.DtpExecutor;
import com.hwoss.cron.service.TaskHandler;
import com.hwoss.suport.utils.ThreadPoolUtils;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hwoss.cron.config.CronAsyncThreadPoolConfig;

/**
 * @author Hwoss
 * @date 2024/05/23
 */
@Service
@Slf4j
public class CronTaskHandler {
    @Autowired
    private TaskHandler handler;

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    private DtpExecutor dtpExecutor = CronAsyncThreadPoolConfig.getXxlCronExecutor();
}
