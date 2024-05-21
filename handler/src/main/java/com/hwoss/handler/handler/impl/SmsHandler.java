package com.hwoss.handler.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.common.constant.CommonConstant;
import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;
import com.common.dto.account.SmsAccount;
import com.common.dto.model.SmsContentModel;
import com.common.enums.ChannelType;
import com.hwoss.handler.domain.MessageTypeSmsConfig;
import com.hwoss.handler.domain.SmsParam;
import com.hwoss.handler.handler.BaseHandler;
import com.hwoss.handler.handler.Handler;
import com.hwoss.suport.dao.SmsRecordDao;
import com.hwoss.suport.service.ConfigService;
import com.hwoss.suport.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class SmsHandler extends BaseHandler implements Handler {

    //    不同渠道进行一个流量配置分配
    private static final Integer AUTO_FLOW_RULE = 0;
    private static final String FLOW_KEY = "msgTypeSmsConfig";
    private static final String FLOW_KEY_PREFIX = "message_type_";

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Autowired
    private ConfigService config;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AccountUtils accountUtils;

    public SmsHandler() {
        channelCode = ChannelType.SMS.getCode();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {
        SmsParam smsParam = SmsParam.builder()
                .messageTemplateId(taskInfo.getMessageTemplateId())
                .content(getSmsContent(taskInfo))
                .phones(taskInfo.getReceivers())
                .build();

        List<MessageTypeSmsConfig> messageTypeSmsConfig = getMessageTypeSmsConfig(taskInfo);
        MessageTypeSmsConfig[] messageTypeSmsConfigs = loadBalance(messageTypeSmsConfig);
        for (MessageTypeSmsConfig typeSmsConfig : messageTypeSmsConfigs) {
            smsParam.setScriptName(typeSmsConfig.getScriptName());
            smsParam.setSendAccountId(typeSmsConfig.getSendAccount());
//            applicationContext.getBean(messageTypeSmsConfigs.)
        }


        return false;
    }


    /**
     * @param taskInfo
     * @return {@link String }
     * 如果存在url就把url拼在后面
     */
    private String getSmsContent(TaskInfo taskInfo) {
        SmsContentModel smsContentModel = (SmsContentModel) taskInfo.getContentModel();
        if (CharSequenceUtil.isNotBlank(smsContentModel.getUrl())) {
            return smsContentModel.getContent() + CharSequenceUtil.SPACE + smsContentModel.getUrl();
        } else {
            return smsContentModel.getContent();
        }
    }

    /**
     * 如模板指定具体的明确账号，则优先发其账号，否则走到流量配置
     * 流量配置每种类型都会有其下发渠道账号的配置(流量占比也会配置里面)
     * 样例：
     * key：msgTypeSmsConfig
     * value：[
     * {
     * "message_type_10": [
     * {
     * "weights": 80,
     * "scriptName": "TencentSmsScript"
     * },
     * {
     * "weights": 20,
     * "scriptName": "YunPianSmsScript"
     * }
     * ]
     * },
     * {
     * "message_type_20": [
     * {
     * "weights": 20,
     * "scriptName": "YunPianSmsScript"
     * }
     * ]
     * },
     * {
     * "message_type_30": [
     * {
     * "weights": 20,
     * "scriptName": "TencentSmsScript"
     * }
     * ]
     * },
     * {
     * "message_type_40": [
     * {
     * "weights": 20,
     * "scriptName": "TencentSmsScript"
     * }
     * ]
     * }
     * ]
     * 通知类短信有两个发送渠道 TencentSmsScript 占80%流量，YunPianSmsScript占20%流量
     * 营销类短信只有一个发送渠道 YunPianSmsScript
     * 验证码短信只有一个发送渠道 TencentSmsScript
     */
    public List<MessageTypeSmsConfig> getMessageTypeSmsConfig(TaskInfo taskInfo) {
//        如果当前默认的的发送用户指定了具体账号，就根据具体账号走
        if (!taskInfo.getSendAccount().equals(AUTO_FLOW_RULE)) {
            SmsAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), SmsAccount.class);
            MessageTypeSmsConfig messageTypeSmsConfig = MessageTypeSmsConfig.builder()
                    .sendAccount(taskInfo.getSendAccount())
                    .scriptName(account.getScriptName())
                    .weights(100)
                    .build();
            return Collections.singletonList(messageTypeSmsConfig);
        }
//        否则就去读取对应流量配置里面的
        String property = config.getProperty(FLOW_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY);
        JSONArray objects = JSON.parseArray(property);
        for (int i = 0; i < objects.size(); i++) {
//            这里没有试过如果增强for会怎么样
            JSONArray jsonArray = objects.getJSONObject(i).getJSONArray(FLOW_KEY_PREFIX + taskInfo.getMsgType());
            if (CollUtil.isNotEmpty(jsonArray)) {
                JSON.parseArray(JSON.toJSONString(jsonArray), MessageTypeSmsConfig.class);
            }
        }
        return new ArrayList<>();
    }


    /**
     * @param messageTypeSmsConfigs
     * @return {@link MessageTypeSmsConfig[] }
     * 根据配置进行负载均衡，返回一个具体渠道商发送渠道和一个备份的，采用随机数
     */
    public MessageTypeSmsConfig[] loadBalance(List<MessageTypeSmsConfig> messageTypeSmsConfigs) {
        int total = 0;
//        计算总权重
        for (MessageTypeSmsConfig channelConfig : messageTypeSmsConfigs) {
            total += channelConfig.getWeights();
        }
        MessageTypeSmsConfig supplier = null;
        MessageTypeSmsConfig supplierBack = null;
        // 生成一个随机数[1,total]，看落到哪个区间，如果随机数大就减掉遍历的区间值
        int index = new Random().nextInt(total) + 1;
//        选出一个，然后选n+1(循环列表)作为备份
        for (int i = 0; i < messageTypeSmsConfigs.size(); i++) {
            if (index <= messageTypeSmsConfigs.get(i).getWeights()) {
                supplier = messageTypeSmsConfigs.get(i);

                int j = (i + 1) % messageTypeSmsConfigs.size();
//                如果列表只有一个
                if (i == j) {
                    return new MessageTypeSmsConfig[]{supplier};
                }
                supplierBack = messageTypeSmsConfigs.get(j);
                return new MessageTypeSmsConfig[]{supplier, supplierBack};
            }
            index -= messageTypeSmsConfigs.get(i).getWeights();
        }
        return new MessageTypeSmsConfig[0];
    }


    //    该渠道没有撤回功能
    @Override
    public void handleRecall(RecallTaskInfo recallTaskInfo) {

    }
}
