package com.hwoss.suport.utils;

import com.alibaba.fastjson.JSON;
import com.common.constant.CommonConstant;
import com.common.dto.account.SmsAccount;
import com.common.enums.ChannelType;
import com.google.common.base.Throwables;
import com.hwoss.suport.dao.ChannelAccountDao;
import com.hwoss.suport.domain.ChannelAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;


@Slf4j
@Configuration
public class AccountUtils {
    @Autowired
    private ChannelAccountDao channelAccountDao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param sendAccountId
     * @param clazz
     * @return {@link T }
     * 根据id获取对应渠道账号的脚本数据写进去
     */
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

    /**
     * @param scriptName
     * @param tClass
     * @return {@link T }
     * 获取所有改渠道的账号进行遍历找到需要的脚本
     */
    public <T> T getSmsAccountByScriptName(String scriptName, Class<T> tClass) {
        try {
            List<ChannelAccount> channelAccountList = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE, ChannelType.SMS.getCode());
            for (ChannelAccount channelAccount : channelAccountList) {

                try {
                    SmsAccount smsAccount = JSON.parseObject(channelAccount.getAccountConfig(), SmsAccount.class);
                    //      此时里面还有渠道商id和渠道商名字
                    if (smsAccount.getScriptName().equals(scriptName)) {
                        //            返回的是 TencentSmsAccount具体的脚本对象
                        return JSON.parseObject(smsAccount.getScriptName(), tClass);
                    }
                } catch (Exception e) {
                    log.error("AccountUtils#getSmsAccount fail! e:{}", Throwables.getStackTraceAsString(e));
                }
            }
        } catch (Exception e) {
            log.error("AccountUtils#getSmsAccount fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        log.error("AccountUtils#getSmsAccount not found!:{}", scriptName);
        return null;
    }
}
