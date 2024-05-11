package com.hwoss.suport.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.common.constant.CommonConstant;

import java.util.Date;

/**
 * @author Hwoss
 * @date 2024/05/11
 * 主要是生成任务的id获取
 */
public class TaskInfoUtils {
    private static final int TYPE_FLAG = 1000000;
    private static final String CODE = "track_code_bid";

    /**
     * 生成任务唯一Id
     *
     * @return hutool包下的字符串随机生成
     */
    public static String generateMessageId() {
        return IdUtil.nanoId();
    }

    /**
     * 生成BusinessId
     * 模板类型+模板ID+当天日期yyyyMMdd
     * (固定16位)
     * 第一位就是类型表示，2-8就是msgId，9-16就是日期表示
     */
    public static Long generateBusinessId(Long templateId, Integer templateType) {
        Integer today = Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN));
        return Long.valueOf(String.format("%d%s", templateType * TYPE_FLAG + templateId, today));
    }

    /**
     * 第二到8位为MessageTemplateId 切割出模板ID
     */
    public static Long getMessageTemplateIdFromBusinessId(Long businessId) {
        return Long.valueOf(String.valueOf(businessId).substring(1, 8));
    }

    /**
     * 从businessId切割出日期
     */
    public static Long getDateFromBusinessId(Long businessId) {
        return Long.valueOf(String.valueOf(businessId).substring(8));
    }

    /**
     * 对url添加平台参数（用于追踪数据)
     * 判断传入的url是否带?如果有就加&，如果没有就加上去
     * track_code_bid= 16位的
     */
    public static String generateUrl(String url, Long templateId, Integer templateType) {
        url = url.trim();
        Long businessId = generateBusinessId(templateId, templateType);
        if (url.indexOf(CommonConstant.QM) == -1) {
            return url + CommonConstant.QM_STRING + CODE + CommonConstant.EQUAL_STRING + businessId;
        } else {
            return url + CommonConstant.AND_STRING + CODE + CommonConstant.EQUAL_STRING + businessId;
        }
    }
}
