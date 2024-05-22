package com.hwoss.handler.receipt.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.common.constant.CommonConstant;
import com.common.dto.account.SmsAccount;
import com.common.enums.ChannelType;
import com.google.common.base.Throwables;
import com.hwoss.handler.receipt.ReceiptMessageStater;
import com.hwoss.handler.script.SmsScript;
import com.hwoss.suport.dao.ChannelAccountDao;
import com.hwoss.suport.dao.SmsRecordDao;
import com.hwoss.suport.domain.ChannelAccount;
import com.hwoss.suport.domain.SmsRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * @author Hwoss
 * @date 2024/05/22
 * 拉取短信回执
 */
@Component
@Slf4j
public class SmsPullReceiptStarterImpl implements ReceiptMessageStater {
    @Autowired
    private ChannelAccountDao channelAccountDao;

    //    这里巧妙在把所有实现SmsScript接口的类全部自动装配进来，key是对应的bean名字，v是实例
    @Autowired
    private Map<String, SmsScript> scriptMap;

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Override
    public void start() {
//        获取是短信渠道的所有渠道账号
        try {
            List<ChannelAccount> channelAccounts = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE, ChannelType.SMS.getCode());
            for (ChannelAccount channelAccount : channelAccounts) {
                //获取渠道账号的配置信息
                SmsAccount smsAccount = JSON.parseObject(channelAccount.getAccountConfig(), SmsAccount.class);
                String scriptName = smsAccount.getScriptName();
                SmsScript smsScript = scriptMap.get(scriptName);
                List<SmsRecord> smsRecords = smsScript.pull(channelAccount.getId().intValue());
                if (CollUtil.isNotEmpty(smsRecords)) {
                    smsRecordDao.saveAll(smsRecords);
                }

            }
        } catch (Exception e) {
            log.error("SmsPullReceiptStarter#start fail:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
