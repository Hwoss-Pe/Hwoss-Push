package com.hwoss.suport.service.impl;

import cn.hutool.setting.dialect.Props;
import com.hwoss.suport.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;

public class ConfigServiceImpl implements ConfigService {

    /**
     * 本地配置
     */
    private static final String PROPERTIES_PATH = "local.properties";
    //    读取配置文件json格式
    private Props props = new Props(PROPERTIES_PATH, StandardCharsets.UTF_8);
//    这边注册中心还是选择apollo

    /**
     * apollo配置
     */
    @Value("${apollo.bootstrap.enabled}")
    private Boolean enableApollo;
    @Value("${apollo.bootstrap.namespaces}")
    private String namespaces;


    /**
     * @param key
     * @param defaultValue
     * @return {@link String }
     * * 读取配置
     * 1、当启动使用了apollo或者nacos，优先读取远程配置
     * 2、当没有启动远程配置，读取本地 local.properties 配置文件的内容
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        return null;
    }
}
