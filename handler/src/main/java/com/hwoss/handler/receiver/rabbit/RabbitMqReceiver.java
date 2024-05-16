package com.hwoss.handler.receiver.rabbit;

import com.hwoss.handler.receiver.service.ConsumeService;
import org.springframework.beans.factory.annotation.Autowired;

public class RabbitMqReceiver {

    private static final String MSG_TYPE_SEND = "send";
    private static final String MSG_TYPE_RECALL = "recall";

    @Autowired
    private ConsumeService consumeService;
}
