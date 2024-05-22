package com.hwoss.handler.script.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.common.dto.account.TencentSmsAccount;
import com.common.enums.SmsStatus;
import com.google.common.base.Throwables;
import com.hwoss.handler.domain.SmsParam;
import com.hwoss.handler.script.SmsScript;
import com.hwoss.suport.domain.SmsRecord;
import com.hwoss.suport.utils.AccountUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("TencentSmsScript")
public class TencentSmsScript implements SmsScript {

    private static final Integer PHONE_NUM = 11;


    @Autowired
    private AccountUtils accountUtils;

    @Override
    public List<SmsRecord> send(SmsParam smsParam) {
        TencentSmsAccount tencentSmsAccount = Objects.nonNull(smsParam.getSendAccountId()) ?
                accountUtils.getAccountById(smsParam.getSendAccountId(), TencentSmsAccount.class)
                : accountUtils.getSmsAccountByScriptName(smsParam.getScriptName(), TencentSmsAccount.class);
        try {
            SmsClient smsClient = init(tencentSmsAccount);
            SendSmsRequest sendSmsRequest = assembleSendReq(smsParam, tencentSmsAccount);
            SendSmsResponse sendSmsResponse = smsClient.SendSms(sendSmsRequest);
            return assembleSendSmsRecord(tencentSmsAccount, sendSmsResponse, smsParam);
        } catch (TencentCloudSDKException e) {
            log.error("TencentSmsScript#send fail:{},params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(smsParam));
            return new ArrayList<>();
        }
    }


    /**
     * 初始化 client
     *
     * @param account
     */
    private SmsClient init(TencentSmsAccount account) {
        Credential cred = new Credential(account.getSecretId(), account.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(account.getUrl());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new SmsClient(cred, account.getRegion(), clientProfile);
    }

    /**
     * 组装发送短信参数
     *
     * @param smsParam
     * @param account
     * @return {@link SendSmsRequest }
     * 其实就是对数据获取到发送的数据和脚本数据的拼接
     */
    private SendSmsRequest assembleSendReq(SmsParam smsParam, TencentSmsAccount account) {
        SendSmsRequest req = new SendSmsRequest();
        String[] phoneNumberSet1 = smsParam.getPhones().toArray(new String[smsParam.getPhones().size() - 1]);
        req.setPhoneNumberSet(phoneNumberSet1);
        req.setSmsSdkAppId(account.getSmsSdkAppId());
        req.setSignName(account.getSignName());
        req.setTemplateId(account.getTemplateId());
        String[] templateParamSet1 = {smsParam.getContent()};
        req.setTemplateParamSet(templateParamSet1);
        req.setSessionContext(IdUtil.fastSimpleUUID());
        return req;
    }


    /**
     * @param account
     * @param response
     * @param smsParam
     * @return {@link List }<{@link SmsRecord }>
     * 把返回的数据和对应的账号信息封装到Record里面
     */
    private List<SmsRecord> assembleSendSmsRecord(TencentSmsAccount account, SendSmsResponse response, SmsParam smsParam) {
        List<SmsRecord> smsRecordList = new ArrayList<>();
        if (Objects.isNull(response) || ArrayUtil.isEmpty(response.getSendStatusSet())) {
            return smsRecordList;
        }
        SendStatus[] sendStatusSet = response.getSendStatusSet();
        for (SendStatus sendStatus : sendStatusSet) {
//        +8613711112222，翻转去前面的11位在翻转回去
            String phone = new StringBuilder
                    (new StringBuilder(sendStatus.getPhoneNumber()).reverse().substring(0, PHONE_NUM))
                    .reverse().toString();

            SmsRecord smsRecord = SmsRecord.builder()
                    .chargingNum(Integer.parseInt(String.valueOf(sendStatus.getFee())))
                    .created(Math.toIntExact(DateUtil.currentSeconds()))  //如果转换超出int范围就会直接报错
                    .messageTemplateId(smsParam.getMessageTemplateId())
                    .msgContent(smsParam.getContent())
                    .phone(Long.valueOf(phone))
                    .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                    .supplierId(account.getSupplierId())
                    .seriesId(sendStatus.getSerialNo())
                    .updated(Math.toIntExact(DateUtil.currentSeconds()))
                    .reportContent(sendStatus.getCode())
                    .supplierName(account.getSupplierName())
                    .status(SmsStatus.SEND_SUCCESS.getCode())//此时发送成功，但是还没收到回执
                    .build();
            smsRecordList.add(smsRecord);
        }
        return smsRecordList;
    }

    /**
     * @param accountId
     * @return {@link List }<{@link SmsRecord }>
     * 拉取回执，类似渠道方的一个ack作用
     */
    @Override
    public List<SmsRecord> pull(Integer accountId) {
        try {
            TencentSmsAccount account = accountUtils.getAccountById(accountId, TencentSmsAccount.class);
            SmsClient client = init(account);
            PullSmsSendStatusRequest req = assemblePullReq(account);
            PullSmsSendStatusResponse resp = client.PullSmsSendStatus(req);
            return assemblePullSmsRecord(account, resp);
        } catch (Exception e) {
            log.error("TencentSmsReceipt#pull fail!{}", Throwables.getStackTraceAsString(e));
            return new ArrayList<>();
        }
    }

    /**
     * 组装 拉取回执 入参
     *
     * @param account
     * @return
     */
    private PullSmsSendStatusRequest assemblePullReq(TencentSmsAccount account) {
        PullSmsSendStatusRequest req = new PullSmsSendStatusRequest();
//        设置了拉取短信发送状态的数量限制为 10 条。
        req.setLimit(10L);
        req.setSmsSdkAppId(account.getSmsSdkAppId());
        return req;
    }

    /**
     * 组装 拉取回执信息
     *
     * @param account
     * @param resp
     * @return
     */
    private List<SmsRecord> assemblePullSmsRecord(TencentSmsAccount account, PullSmsSendStatusResponse resp) {
        List<SmsRecord> smsRecordList = new ArrayList<>();
        if (Objects.nonNull(resp) && Objects.nonNull(resp.getPullSmsSendStatusSet()) && resp.getPullSmsSendStatusSet().length > 0) {
            for (PullSmsSendStatus pullSmsSendStatus : resp.getPullSmsSendStatusSet()) {
                SmsRecord smsRecord = SmsRecord.builder()
                        .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                        .messageTemplateId(0L)
                        .phone(Long.valueOf(pullSmsSendStatus.getSubscriberNumber()))
                        .supplierId(account.getSupplierId())
                        .supplierName(account.getSupplierName())
                        .msgContent("")
                        .seriesId(pullSmsSendStatus.getSerialNo())
                        .chargingNum(0)
//                        主要看响应码是否进行一个下发成功
                        .status("SUCCESS".equals(pullSmsSendStatus.getReportStatus()) ? SmsStatus.RECEIVE_SUCCESS.getCode() : SmsStatus.RECEIVE_FAIL.getCode())
                        .reportContent(pullSmsSendStatus.getDescription())
                        .updated(Math.toIntExact(pullSmsSendStatus.getUserReceiveTime()))
                        .created(Math.toIntExact(DateUtil.currentSeconds()))
                        .build();
                smsRecordList.add(smsRecord);
            }
        }
        return smsRecordList;
    }
}
