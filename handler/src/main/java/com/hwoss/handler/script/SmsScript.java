package com.hwoss.handler.script;

import com.hwoss.handler.domain.SmsParam;
import com.hwoss.suport.domain.SmsRecord;

import java.util.List;

public interface SmsScript {


    /**
     * 发送短信
     *
     * @param smsParam
     * @return 渠道商发送接口返回值
     */
    List<SmsRecord> send(SmsParam smsParam);


    /**
     * 拉取回执
     *
     * @param id 渠道账号的ID
     * @return 渠道商回执接口返回值
     */
    List<SmsRecord> pull(Integer id);
}
