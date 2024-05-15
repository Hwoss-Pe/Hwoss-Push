package com.hwoss.handler.utils;

import com.common.domain.TaskInfo;
import com.common.enums.ChannelType;
import com.common.enums.EnumsUtils;
import com.common.enums.MessageType;

import javax.mail.Message;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/15
 * 获取所有的消费者组，对于kafka来说目前
 */
public class GroupIdMappingUtils {
    private GroupIdMappingUtils() {

    }

    /**
     * @return {@link List }<{@link String }>
     * 获取的思路就是由于mq里面的topic就是拼接好的，所以这里直接再拼接一次然后遍历收集
     */
    public static List<String> getAllGroupId() {
        List<String> groupIds = new ArrayList<>();
        for (ChannelType channelType : ChannelType.values()) {
            for (MessageType messageType : MessageType.values()) {
//             //例子比如 ： sms.notice   用的是发送渠道+发送类型组合
                groupIds.add(channelType.getCodeEn() + '.' + messageType.getCodeEn());
            }
        }
        return groupIds;
    }

    /**
     * 根据TaskInfo获取当前消息的groupId
     *
     * @param taskInfo
     * @return
     */
    public static String getGroupIdByTaskInfo(TaskInfo taskInfo) {
        String channelCodeEn = EnumsUtils.getEnumByCode(taskInfo.getSendChannel(), ChannelType.class).getCodeEn();
        String messageCodeEn = EnumsUtils.getEnumByCode(taskInfo.getTemplateType(), MessageType.class).getCodeEn();
        return channelCodeEn + "." + messageCodeEn;
    }
}
