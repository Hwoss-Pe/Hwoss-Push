package com.hwoss.handler.script.impl;

import com.hwoss.handler.domain.SmsParam;
import com.hwoss.handler.script.SmsScript;
import com.hwoss.suport.domain.SmsRecord;

import java.util.List;

public class TencentSmsScript implements SmsScript {

    @Override
    public List<SmsRecord> send(SmsParam smsParam) {
        return null;
    }

    @Override
    public List<SmsRecord> pull(Integer id) {
        return null;
    }
}
