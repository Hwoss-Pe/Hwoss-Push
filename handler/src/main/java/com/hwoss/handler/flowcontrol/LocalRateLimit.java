package com.hwoss.handler.flowcontrol;

import com.common.enums.RateLimitStrategy;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;


@Target(ElementType.TYPE) //表示注解可以用于接口和类，是一个元注解
@Retention(RetentionPolicy.RUNTIME)//运行时候保留，为了反射进行访问
@Documented  ///生成的 JavaDoc 中包含此注解的信息
@Service
public @interface LocalRateLimit {
    //    注解里面定义了一个属性，并且设置默认值
    RateLimitStrategy rateLimitStrategy() default RateLimitStrategy.REQUEST_RATE_LIMIT;
}
