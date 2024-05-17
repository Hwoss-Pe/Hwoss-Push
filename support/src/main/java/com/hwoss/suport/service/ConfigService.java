package com.hwoss.suport.service;

public interface ConfigService {

    /**
     * @param key
     * @param defaultValue
     * @return {@link String }
     * 读取配置类的相对接口
     */
    String getProperty(String key, String defaultValue);

}
