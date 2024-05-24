package com.hwoss.cron.service.impl;

import com.hwoss.cron.service.TaskHandler;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskHandlerImpl implements TaskHandler {
    @Override
    public void handle(long messageTemplateId) {

    }
}
