package com.hwoss.cron.handler;

import com.dtp.core.thread.DtpExecutor;
import com.hwoss.cron.service.TaskHandler;
import com.hwoss.suport.utils.ThreadPoolUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
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

    @XxlJob("SendJob")
    public void execute() {
        log.info("CronTaskHandler#execute messageTemplateId:{} cron exec!", XxlJobHelper.getJobParam());
//        获取到动态线程池后把他注册到spring进行 优雅关闭
        threadPoolUtils.register(dtpExecutor);
//解决具体的时候传进模板id
        Long messageTemplateId = Long.valueOf(XxlJobHelper.getJobParam());
        dtpExecutor.execute(() -> handler.handle(messageTemplateId));
    }
}
