package com.hwoss.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.EnumUtil;
import com.alibaba.fastjson.JSON;
import com.common.domain.SimpleAnchorInfo;
import com.common.enums.AnchorState;
import com.common.enums.ChannelType;
import com.common.enums.EnumsUtils;
import com.common.enums.SmsStatus;
import com.hwoss.service.api.pojo.TraceResponse;
import com.hwoss.service.api.service.TraceService;
import com.hwoss.suport.dao.MessageTemplateDao;
import com.hwoss.suport.dao.SmsRecordDao;
import com.hwoss.suport.domain.MessageTemplate;
import com.hwoss.suport.domain.SmsRecord;
import com.hwoss.suport.utils.RedisUtils;
import com.hwoss.suport.utils.TaskInfoUtils;
import com.hwoss.web.service.DataService;
import com.hwoss.web.utils.AnchorStateUtils;
import com.hwoss.web.vo.DataParam;
import com.hwoss.web.vo.SmsTimeLineVo;
import com.hwoss.web.vo.UserTimeLineVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Autowired
    private TraceService traceService;


    @Override
    public UserTimeLineVo getTraceMessageInfo(String messageId) {
        TraceResponse traceResponse = traceService.traceByMessageId(messageId);
        if (CollUtil.isEmpty(traceResponse.getData())) {
            return UserTimeLineVo.builder().items(new ArrayList<>()).build();
        }
        return buildUserTimeLineVo(traceResponse.getData());
    }

    @Override
    public UserTimeLineVo getTraceUserInfo(String receiver) {
        List<String> userInfoList = redisUtils.lRange(receiver, 0, -1);
        if (CollUtil.isEmpty(userInfoList)) {
            return UserTimeLineVo.builder().items(new ArrayList<>()).build();
        }

        // 0. 按时间排序
        List<SimpleAnchorInfo> sortAnchorList = userInfoList.stream().map(s -> JSON.parseObject(s, SimpleAnchorInfo.class)).sorted(Comparator.comparing(SimpleAnchorInfo::getTimestamp).reversed()).collect(Collectors.toList());
        return buildUserTimeLineVo(sortAnchorList);
    }

    @Override
    public SmsTimeLineVo getTraceSmsInfo(DataParam dataParam) {
        Integer sendDate = Integer.valueOf(DateUtil.format(new Date(dataParam.getDateTime() * 1000L), DatePattern.PURE_DATE_PATTERN));
        List<SmsRecord> smsRecordList = smsRecordDao.findByPhoneAndSendDate(Long.valueOf(dataParam.getReceiver()), sendDate);
        if (CollUtil.isEmpty(smsRecordList)) {
            return SmsTimeLineVo.builder().items(Arrays.asList(SmsTimeLineVo.ItemsVO.builder().build())).build();
        }

        Map<String, List<SmsRecord>> maps = smsRecordList.stream().collect(Collectors.groupingBy(o -> o.getPhone() + o.getSeriesId()));
//        返回SmsTimeLineVo，把相同的手机号做key，对应记录做记录
        List<SmsTimeLineVo.ItemsVO> items = new ArrayList<>();
//      只取得最后一次发送,因为我只关心这段sms记录最后的下发状态
        for (Map.Entry<String, List<SmsRecord>> entry : maps.entrySet()) {
            SmsTimeLineVo.ItemsVO itemsVO = SmsTimeLineVo.ItemsVO.builder().build();
            for (SmsRecord smsRecord : entry.getValue()) {
                // 发送记录 messageTemplateId >0 ,回执记录 messageTemplateId =0
                if (smsRecord.getMessageTemplateId() > 0) {
                    itemsVO.setBusinessId(String.valueOf(smsRecord.getMessageTemplateId()));
                    itemsVO.setContent(smsRecord.getMsgContent());
                    itemsVO.setSendType(EnumsUtils.getDescription(smsRecord.getStatus(), SmsStatus.class));
                    itemsVO.setSendTime(DateUtil.format(new Date(smsRecord.getCreated() * 1000L), DatePattern.NORM_DATETIME_PATTERN));
                } else {
                    itemsVO.setReceiveType(EnumsUtils.getDescription(smsRecord.getStatus(), SmsStatus.class));
                    itemsVO.setReceiveContent(smsRecord.getReportContent());
                    itemsVO.setReceiveTime(DateUtil.format(new Date(smsRecord.getUpdated() * 1000L), DatePattern.NORM_DATETIME_PATTERN));
                }
            }
            items.add(itemsVO);
        }
        return SmsTimeLineVo.builder()
                .items(items).build();
    }

    public UserTimeLineVo buildUserTimeLineVo(List<SimpleAnchorInfo> sortAnchorList) {
//        统计相同业务（类似订单Id）相同的数据， {"businessId":[{businessId,state,timeStamp},{businessId,state,timeStamp}]}
        Map<String, List<SimpleAnchorInfo>> map = MapUtil.newHashMap();
        for (SimpleAnchorInfo simpleAnchorInfo : sortAnchorList) {
            List<SimpleAnchorInfo> simpleAnchorInfos = map.get(simpleAnchorInfo.getBusinessId().toString());
            if (CollUtil.isEmpty(simpleAnchorInfos)) {
                simpleAnchorInfos = new ArrayList<>();
            }
            simpleAnchorInfos.add(simpleAnchorInfo);
            map.put(simpleAnchorInfo.getBusinessId().toString(), simpleAnchorInfos);
        }
        // 2. 封装vo
        List<UserTimeLineVo.ItemsVO> items = new ArrayList<>();
        for (Map.Entry<String, List<SimpleAnchorInfo>> entry : map.entrySet()) {
            Long businessId = TaskInfoUtils.getMessageTemplateIdFromBusinessId(Long.valueOf(entry.getKey()));
            MessageTemplate messageTemplate = messageTemplateDao.findById(businessId).orElse(null);
            if (Objects.isNull(messageTemplate)) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (SimpleAnchorInfo simpleAnchorInfo : entry.getValue()) {
//            用回车键进行一个分离不同的锚点
                if (AnchorState.RECEIVE.getCode().equals(simpleAnchorInfo.getState())) {
                    sb.append(StrPool.CRLF);
                }
                String startTime = DateUtil.format(new Date(simpleAnchorInfo.getTimestamp()), DatePattern.NORM_DATETIME_PATTERN);
                String stateDescription = AnchorStateUtils.getDescriptionByState(messageTemplate.getSendChannel(), simpleAnchorInfo.getState());
                sb.append(startTime).append(StrPool.C_COLON).append(stateDescription).append("==>");
            }

            for (String detail : sb.toString().split(StrPool.CRLF)) {
//           然后分别创建对象处理，这里用list的思路去创建也行
                if (CharSequenceUtil.isNotBlank(detail)) {
                    UserTimeLineVo.ItemsVO itemsVO = UserTimeLineVo.ItemsVO.builder()
                            .businessId(entry.getKey())
                            .sendType(EnumsUtils.getEnumByCode(messageTemplate.getSendChannel(), ChannelType.class).getDescription())
                            .creator(messageTemplate.getCreator())
                            .title(messageTemplate.getName())
                            .detail(detail)
                            .build();
                    items.add(itemsVO);
                }
            }
        }
        return UserTimeLineVo.builder().items(items).build();
    }
}
