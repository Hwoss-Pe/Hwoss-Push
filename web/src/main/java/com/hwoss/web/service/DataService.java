package com.hwoss.web.service;

import com.hwoss.web.vo.DataParam;
import com.hwoss.web.vo.SmsTimeLineVo;
import com.hwoss.web.vo.UserTimeLineVo;

public interface DataService {
    /**
     * 获取全链路追踪 消息自身维度信息
     *
     * @param messageId 消息
     * @return
     */
    UserTimeLineVo getTraceMessageInfo(String messageId);

    /**
     * 获取全链路追踪 用户维度信息
     *
     * @param receiver 接收者
     * @return
     */
    UserTimeLineVo getTraceUserInfo(String receiver);


    /**
     * 获取短信下发记录
     *
     * @param dataParam
     * @return
     */
    SmsTimeLineVo getTraceSmsInfo(DataParam dataParam);

}
