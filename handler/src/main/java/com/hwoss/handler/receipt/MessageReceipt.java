package com.hwoss.handler.receipt;

import com.google.common.base.Throwables;
import com.hwoss.suport.config.SupportThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/22
 * 消息接收入口
 */
@Slf4j
@Component
public class MessageReceipt {
        @Autowired
        private List<ReceiptMessageStater> receiptMessageStaterList;

        /**
         * 拉取信息入口，采用单线程池形式
         */
        @PostConstruct
        private void init() {
                SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
                        while (true) {
                                try {
                                        for (ReceiptMessageStater receiptMessageStater : receiptMessageStaterList) {
                                                //receiptMessageStater.start();
                                        }
                                        Thread.sleep(2000);
                                } catch (Exception e) {
                                        log.error("MessageReceipt#init fail:{}", Throwables.getStackTraceAsString(e));
                                        Thread.currentThread().interrupt();
                                }
                        }
                });
        }
}
