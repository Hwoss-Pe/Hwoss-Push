package com.hwoss.service.impl.serivce;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.common.constant.FuncConstant;
import com.common.domain.SimpleAnchorInfo;
import com.common.enums.RespStatusEnum;
import com.hwoss.service.api.pojo.TraceResponse;
import com.hwoss.service.api.service.TraceService;
import com.hwoss.suport.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Hwoss
 * @date 2024/05/22
 * 获取redis里面message，也就是一条信息对应的所有锚点按照时间时间排序
 */
@Service
@Slf4j
public class TraceServiceImpl implements TraceService {
    @Autowired
    private RedisUtils redisUtils;

    /**
     * @param messageId
     * @return {@link TraceResponse }
     */
    @Override
    public TraceResponse traceByMessageId(String messageId) {
        if (Objects.isNull(messageId)) {
            return new TraceResponse(RespStatusEnum.CLIENT_BAD_PARAMETER.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETER.getMsg(), null);
        }
        String redisMessageKey = CharSequenceUtil.join(StrUtil.COLON, FuncConstant.CACHE_KEY_PREFIX, FuncConstant.MESSAGE_ID, messageId);
//        就是:做分割进行连接
        List<String> messageList = redisUtils.lRange(redisMessageKey, 0, -1);
        if (CollUtil.isEmpty(messageList)) {
            return new TraceResponse(RespStatusEnum.FAIL.getCode(), RespStatusEnum.FAIL.getMsg(), null);
        }
//        对于锚点信息按照时间排序
        List<SimpleAnchorInfo> sortAnchorList = messageList.stream().map(s -> JSON.parseObject(s, SimpleAnchorInfo.class))
                .sorted((o1, o2) -> Math.toIntExact(o1.getTimestamp() - o2.getTimestamp()))
                .collect(Collectors.toList());

        return new TraceResponse(RespStatusEnum.SUCCESS.getCode(), RespStatusEnum.SUCCESS.getMsg(), sortAnchorList);

    }
}

