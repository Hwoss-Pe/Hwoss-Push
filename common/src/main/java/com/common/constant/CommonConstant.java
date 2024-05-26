package com.common.constant;

public class CommonConstant {
    public static final String QM_STRING = "?";
    public static final char QM = '?';
    public static final String EQUAL_STRING = "=";
    public static final String AND_STRING = "&";
    /**
     * boolean转换
     */
    public static final Integer TRUE = 1;
    public static final Integer FALSE = 0;

    /**
     * HTTP 请求方法
     */
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";


    /**
     * 环境常量
     */
    public static final String ENV_DEV = "dev";
    public static final String ENV_TEST = "test";

    /**
     * JSON默认值
     */
    public static final String EMPTY_JSON_OBJECT = "{}";
    public static final String EMPTY_VALUE_JSON_ARRAY = "[]";
    /**
     * 日期相关
     */
    public static final String CRON_FORMAT = "ss mm HH dd MM ? yyyy-yyyy";
    public static final Long ONE_DAY_SECOND = 86400L;

}
