package com.hwoss.cron.service;

public interface TaskHandler {
    void handle(long messageTemplateId);
}
