package com.hwoss.cron.xxl.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.common.enums.RespStatusEnum;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import com.hwoss.cron.xxl.constant.XxlJobConstant;
import com.hwoss.cron.xxl.pojo.XxlJobGroup;
import com.hwoss.cron.xxl.pojo.XxlJobInfo;
import com.hwoss.cron.xxl.service.CronTaskService;

import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CronTaskServiceImpl implements CronTaskService {
    @Value("${xxl.job.admin.username}")
    private String xxlUserName;

    @Value("${xxl.job.admin.password}")
    private String xxlPassword;

    @Value("${xxl.job.admin.addresses}")
    private String xxlAddresses;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public BasicResultVo saveCronTask(XxlJobInfo xxlJobInfo) {
        return null;
    }

    @Override
    public BasicResultVo deleteCronTask(Integer taskId) {
        return null;
    }

    @Override
    public BasicResultVo startCronTask(Integer taskId) {
        return null;
    }

    @Override
    public BasicResultVo stopCronTask(Integer taskId) {
        return null;
    }

    /**
     * @param appName
     * @param title
     * @return {@link BasicResultVo }
     * 对应版本2.3.0  JobGroupController
     */
    @Override
    public BasicResultVo getGroupId(String appName, String title) {
        String path = xxlAddresses + XxlJobConstant.JOB_GROUP_PAGE_LIST;
        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("appname", appName);
        params.put("title", title);
        HttpResponse response = null;
        try {
            response = HttpRequest.post(path).form(params).cookie(getCookie()).execute();
            if (Objects.isNull(response)) {
                return BasicResultVo.fail(RespStatusEnum.SERVICE_ERROR);
            }
            Integer id = JSON.parseObject(response.body()).getJSONArray("data").getJSONObject(0).getInteger("id");
            if (response.isOk() && Objects.nonNull(id)) {
                return BasicResultVo.success(id);
            }
        } catch (Exception e) {
            log.error("CronTaskService#getGroupId fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(params), JSON.toJSONString(response.body()));
        }
        invalidateCookie();
        return BasicResultVo.fail(RespStatusEnum.SERVICE_ERROR, JSON.toJSONString(response.body()));

    }

    @Override
    public BasicResultVo createGroup(XxlJobGroup xxlJobGroup) {
        //        思路是吧对象映射成map就可以发送携带参数
        String path = xxlAddresses + XxlJobConstant.JOB_GROUP_INSERT_URL;
        Map<String, Object> map = JSON.parseObject(xxlJobGroup.toString(), Map.class);
        HttpResponse response = null;
        ReturnT returnT = null;
        try {
            response = HttpRequest.post(path).form(map).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && ReturnT.SUCCESS_CODE == returnT.getCode()) {
                return BasicResultVo.success();
            }
        } catch (Exception e) {
            log.error("CronTaskService#createGroup fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(map), JSON.toJSONString(returnT));
        }
        invalidateCookie();
        return null;
    }


    //每次请求xxl的时候都会进行cookie的访问获取，然后进行销毁
    public String getCookie() {
        //判断redis里面是否有对应的数据
        String cookie = redisTemplate.opsForValue().get(XxlJobConstant.COOKIE_PREFIX + xxlUserName);
        if (!Objects.isNull(cookie)) {
//            如果缓存中没有进行储存的token就进行登录验证
            return cookie;
        }
        Map<String, Object> map = new HashMap<>();
        String path = xxlAddresses + XxlJobConstant.LOGIN_URL;
        map.put("userName", xxlUserName);
        map.put("password", xxlPassword);
        map.put("randomCode", IdUtil.randomUUID());
        HttpResponse response = null;
        try {
            response = HttpRequest.post(path).form(map).execute();
            if (response.isOk()) {
                List<HttpCookie> cookies = response.getCookies();
                StringBuilder stringBuilder = new StringBuilder();
                for (HttpCookie httpCookie : cookies) {
                    stringBuilder.append(httpCookie);
                }
                redisTemplate.opsForValue().set(XxlJobConstant.COOKIE_PREFIX + xxlUserName, stringBuilder.toString());
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            log.error("CronTaskService#createGroup getCookie,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(map), JSON.toJSONString(response));
        }
        return null;
    }

    /**
     * 清除缓存的 cookie
     */
    private void invalidateCookie() {
        redisTemplate.delete(XxlJobConstant.COOKIE_PREFIX + xxlUserName);
    }
}
