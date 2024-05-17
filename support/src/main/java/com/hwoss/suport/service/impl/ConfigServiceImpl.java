package com.hwoss.suport.service.impl;

import cn.hutool.setting.dialect.Props;
import com.ctrip.framework.apollo.Config;
import com.hwoss.suport.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
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
        if (Boolean.TRUE.equals(enableApollo)) {
            Config config = com.ctrip.framework.apollo.ConfigService.getConfig(namespaces);
//            这个放啊传入的value就是去获取配置key的值，如果获取不到就返回默认
            return config.getProperty(key, defaultValue);
        }
        return props.getProperty(key, defaultValue);
    }
}
