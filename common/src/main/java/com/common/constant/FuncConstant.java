package com.common.constant;

public class FuncConstant {
    /**
     * 接口限制 最多的人数
     */
    public static final Integer BATCH_RECEIVER_SIZE = 100;

    /**
     * 链路追踪缓存的key标识
     */
    public static final String CACHE_KEY_PREFIX = "Hwoss";
    public static final String MESSAGE_ID = "MessageId";
    /**
     * 消息模板常量；
     * 如果新建模板/账号时，没传入则用该常量
     */
    public static final String DEFAULT_CREATOR = "hwoss";
    public static final String DEFAULT_UPDATOR = "hwoss";
    public static final String DEFAULT_TEAM = "Hwoss";
    public static final String DEFAULT_AUDITOR = "Hwoss";
}
