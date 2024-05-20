package com.hwoss.suport.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.hwoss.suport.dao.ChannelAccountDao;
import com.hwoss.suport.domain.ChannelAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.Optional;


@Slf4j
@Configuration
public class AccountUtils {
    @Autowired
    private ChannelAccountDao channelAccountDao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public <T> T getAccountById(Integer sendAccountId, Class<T> clazz) {
        try {
            Optional<ChannelAccount> optionalChannelAccount = channelAccountDao.findById(Long.valueOf(sendAccountId));
            if (optionalChannelAccount.isPresent()) {
                ChannelAccount channelAccount = optionalChannelAccount.get();
                return JSON.parseObject(channelAccount.getAccountConfig(), clazz);
            }
        } catch (Exception e) {
            log.error("AccountUtils#getAccount fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }

}
