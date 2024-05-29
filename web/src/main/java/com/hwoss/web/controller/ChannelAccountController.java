package com.hwoss.web.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.common.constant.FuncConstant;
import com.common.enums.RespStatusEnum;
import com.hwoss.suport.domain.ChannelAccount;
import com.hwoss.web.aspect.LogAspect;
import com.hwoss.web.aspect.ResultAspect;
import com.hwoss.web.exception.CommonException;
import com.hwoss.web.service.ChannelAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hwoss
 * @date 2024/05/29
 * 对于渠道发送账号的接口
 */
@Slf4j
@LogAspect
@ResultAspect
@RestController
@RequestMapping("/account")
@Api("渠道账号管理接口")
public class ChannelAccountController {
    @Autowired
    private ChannelAccountService channelAccountService;


    /**
     * 如果Id存在，则修改
     * 如果Id不存在，则保存
     */
    @PostMapping("/save")
    public ChannelAccount saveOrUpdate(@RequestBody ChannelAccount channelAccount) {
        if (CharSequenceUtil.isBlank(channelAccount.getCreator())) {
            throw new CommonException(RespStatusEnum.NO_LOGIN.getCode(), RespStatusEnum.NO_LOGIN.getMsg());
        }
        channelAccount.setCreator(CharSequenceUtil.isBlank(channelAccount.getCreator()) ? FuncConstant.DEFAULT_CREATOR : channelAccount.getCreator());

        return channelAccountService.save(channelAccount);
    }

    /**
     * 根据渠道标识查询渠道账号相关的信息
     */
    @GetMapping("/queryByChannelType")
    public List<ChannelAccount> query(Integer channelType, String creator) {
        if (CharSequenceUtil.isBlank(creator)) {
            throw new CommonException(RespStatusEnum.NO_LOGIN.getCode(), RespStatusEnum.NO_LOGIN.getMsg());
        }
        creator = CharSequenceUtil.isBlank(creator) ? FuncConstant.DEFAULT_CREATOR : creator;

        return channelAccountService.queryByChannelType(channelType, creator);

    }

    /**
     * 所有的渠道账号信息
     */
    @GetMapping("/list")
    public List<ChannelAccount> list(String creator) {
        if (CharSequenceUtil.isBlank(creator)) {
            throw new CommonException(RespStatusEnum.NO_LOGIN.getCode(), RespStatusEnum.NO_LOGIN.getMsg());

        }
        creator = CharSequenceUtil.isBlank(creator) ? FuncConstant.DEFAULT_CREATOR : creator;

        return channelAccountService.list(creator);
    }

    /**
     * 根据Id删除
     * id多个用逗号分隔开
     */
    @DeleteMapping("delete/{id}")
    public void deleteByIds(@PathVariable("id") String id) {
        if (CharSequenceUtil.isNotBlank(id)) {
            List<Long> idList = Arrays.stream(id.split(StrPool.COMMA)).map(Long::valueOf).collect(Collectors.toList());
            channelAccountService.deleteByIds(idList);
        }
    }
}
