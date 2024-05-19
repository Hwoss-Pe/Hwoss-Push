package com.hwoss.handler.deduplication;

import com.hwoss.handler.deduplication.builder.Builder;
import com.hwoss.handler.deduplication.service.DeduplicationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hwoss
 * @date 2024/05/19
 * 在创建两个抽象类进行初始化注册进来
 */
@Service
public class DeduplicationHolder {
    //    分别是存储对一个的去重类型，一个对应具体的构建类，一个对应去重服务，
    private final Map<Integer, Builder> builderHolder = new HashMap<>(4);

    private final Map<Integer, DeduplicationService> serviceHolder = new HashMap<>(4);


    public void putBuilder(Integer key, Builder builder) {
        builderHolder.put(key, builder);
    }

    public void putService(Integer key, DeduplicationService service) {
        serviceHolder.put(key, service);
    }

    public Builder selectBuilder(Integer key) {
        return builderHolder.get(key);
    }

    public DeduplicationService selectService(Integer key) {
        return serviceHolder.get(key);
    }

}
