package com.hwoss.web.aspect;


import com.common.vo.BasicResultVo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * @author Hwoss
 * @date 2024/05/28
 * 拦截所有被注解的返回数据的时候进行封装
 */
@ControllerAdvice(basePackages = "com.hwoss.web.controller")
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final String RETURN_CLASS = "BasicResultVo";

    //定义生效范围
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(ResultAspect.class) || returnType.getContainingClass().isAnnotationPresent(ResultAspect.class);
    }

    //具体处理逻辑
    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (Objects.nonNull(data) && Objects.nonNull(data.getClass())) {
            String simpleName = data.getClass().getSimpleName();
            if (simpleName.equalsIgnoreCase(RETURN_CLASS)) {
                return data;
            }
        }
        return BasicResultVo.success(data);
    }
}
