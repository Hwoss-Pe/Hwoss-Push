package com.hwoss.web.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.hwoss.web.aspect.LogAspect;
import com.hwoss.web.aspect.ResultAspect;
import com.hwoss.web.service.DataService;
import com.hwoss.web.vo.DataParam;
import com.hwoss.web.vo.SmsTimeLineVo;
import com.hwoss.web.vo.UserTimeLineVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Hwoss
 * @date 2024/05/29
 * 进行链路追踪接口
 */
@Slf4j
@LogAspect
@ResultAspect
@RestController
@RequestMapping("/trace")
@Api("链路追踪接口")
public class DataController {
    @Autowired
    private DataService dataService;

    @PostMapping("/message")
    public UserTimeLineVo getMessageData(@RequestBody DataParam dataParam) {
        if (Objects.isNull(dataParam) || CharSequenceUtil.isBlank(dataParam.getMessageId())) {
            return UserTimeLineVo.builder().items(new ArrayList<>()).build();
        }
        return dataService.getTraceMessageInfo(dataParam.getMessageId());
    }

    @PostMapping("/user")
    public UserTimeLineVo getUserData(@RequestBody DataParam dataParam) {
        if (Objects.isNull(dataParam) || CharSequenceUtil.isBlank(dataParam.getReceiver())) {
            return UserTimeLineVo.builder().items(new ArrayList<>()).build();
        }
        return dataService.getTraceUserInfo(dataParam.getReceiver());
    }


    @PostMapping("/sms")
    public SmsTimeLineVo getSmsData(@RequestBody DataParam dataParam) {
        if (Objects.isNull(dataParam) || Objects.isNull(dataParam.getDateTime()) || CharSequenceUtil.isBlank(dataParam.getReceiver())) {
            return SmsTimeLineVo.builder().items(Lists.newArrayList()).build();
        }
        return dataService.getTraceSmsInfo(dataParam);
    }


}
