package com.hwoss.handler.flowcontrol;


import cn.hutool.core.util.EnumUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.constant.CommonConstant;
import com.common.domain.TaskInfo;
import com.common.enums.ChannelType;
import com.common.enums.EnumsUtils;
import com.common.enums.RateLimitStrategy;
import com.google.common.util.concurrent.RateLimiter;
import com.hwoss.suport.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FlowControlFactory implements ApplicationContextAware {

    private static final String FLOW_CONTROL_KEY = "flowControlRule";
    private static final String FLOW_CONTROL_PREFIX = "flow_control_";

    private ApplicationContext applicationContext;

    private final Map<RateLimitStrategy, FlowControlService> flowControlServiceMap = new ConcurrentHashMap<>();


    @Autowired
    private ConfigService config;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * 初始化就把对应的限流方式和实现类写进入map,利用上下文件去找由注解的类，
     */
    @PostConstruct
    public void init() {
        Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(LocalRateLimit.class);
        map.forEach((name, service) ->
                    {
                        if (service instanceof FlowControlService) {
                            Class<?> targetClass = AopUtils.getTargetClass(service);
                            LocalRateLimit annotation = targetClass.getAnnotation(LocalRateLimit.class);
                            RateLimitStrategy rateLimitStrategy = annotation.rateLimitStrategy();
                            flowControlServiceMap.put(rateLimitStrategy, (FlowControlService) service);
                        }
                    }
        );
    }

    /**
     * 得到限流值的配置
     * <p>
     * apollo配置样例     key：flowControl value：{"flow_control_40":1}
     * <p>
     *
     * @param channelCode
     */
    public Double getRareLimitConfig(Integer channelCode) {
        String flowControlConfig = config.getProperty(FLOW_CONTROL_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        JSONObject jsonObject = JSON.parseObject(flowControlConfig);
        if (Objects.isNull(jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode))) {
            return null;
        }
        return jsonObject.getDouble(FLOW_CONTROL_PREFIX + channelCode);
    }


    public void flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter;
        Double rateInitValue = flowControlParam.getRateInitValue();
        Double rateLimitConfig = getRareLimitConfig(taskInfo.getSendChannel());
//        初始化的限流值和配置限流值冲突的话以配置中心的为准
        if (Objects.nonNull(rateLimitConfig) && !rateLimitConfig.equals(rateInitValue)) {
//            根据返回的每秒请求数创建对应的限流器
            rateLimiter = RateLimiter.create(rateLimitConfig);
            flowControlParam.setRateLimiter(rateLimiter);
            flowControlParam.setRateInitValue(rateLimitConfig);
        }
        FlowControlService flowControlService = flowControlServiceMap.get(flowControlParam.getRateLimitStrategy());
        if (Objects.isNull(flowControlService)) {
            log.error("没有找到对应的单机限流策略");
            return;
        }
        Double costTime = flowControlService.flowControl(taskInfo, flowControlParam);
        if (costTime > 0) {
            log.info("consumer {} flow control time {}",
                     EnumsUtils.getEnumByCode(taskInfo.getSendChannel(), ChannelType.class).getDescription(), costTime);
        }
    }

}


