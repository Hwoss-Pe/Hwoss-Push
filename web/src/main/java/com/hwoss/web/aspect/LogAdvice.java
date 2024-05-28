package com.hwoss.web.aspect;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hwoss.web.vo.RequestLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
public class LogAdvice {
    /**
     * 同一个请求的KEY
     */
    private static final String REQUEST_ID_KEY = "request_unique_id";
    @Autowired
    private HttpServletRequest request;

    //    去切有这个注解的方法和类
    @Pointcut("@within(com.hwoss.web.aspect.LogAspect)||@annotation(com.hwoss.web.aspect.LogAspect)")
    public void execute() {
    }

    @Before("execute()")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        this.print(signature, joinPoint.getArgs());
    }

    /**
     * 异常通知
     *
     * @param ex
     */
    @AfterThrowing(value = "execute()", throwing = "ex")
    public void doAfterThrowingAdvice(Throwable ex) {
        print(ex);
    }


    /**
     * @param signature 请求信息打印
     */
    public void print(MethodSignature signature, Object[] argObs) {
        RequestLogDTO logVo = new RequestLogDTO();
        logVo.setId(IdUtil.fastUUID());
        request.setAttribute(REQUEST_ID_KEY, logVo.getId());
        logVo.setUri(request.getRequestURI());
        logVo.setMethod(request.getMethod());
        List<Object> args = Lists.newArrayList();
        //过滤掉一些不能转为json字符串的参数
        Arrays.stream(argObs).forEach(e -> {
            if (e instanceof MultipartFile || e instanceof HttpServletRequest
                    || e instanceof HttpServletResponse || e instanceof BindingResult) {
                return;
            }
            args.add(e);
        });
        logVo.setArgs(args.toArray());
        logVo.setPath(signature.getDeclaringTypeName() + "." + signature.getMethod().getName());
        log.info(JSON.toJSONString(logVo));
        logVo.setRemoteAddr(request.getRemoteAddr());

    }

    /**
     * @param ex 对错误信息进行打印
     */
    public void print(Throwable ex) {
        JSONObject logVo = new JSONObject();
        logVo.put("id", request.getAttribute(REQUEST_ID_KEY));
        log.error(JSON.toJSONString(logVo), ex);
    }
}
