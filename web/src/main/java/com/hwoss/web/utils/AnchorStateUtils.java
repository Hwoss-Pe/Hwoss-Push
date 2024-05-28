package com.hwoss.web.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.EnumUtil;
import com.common.enums.AnchorState;
import com.common.enums.EnumsUtils;

public class AnchorStateUtils {

    private AnchorStateUtils() {

    }

    /**
     * 根据渠道和点位 获取点位的描述
     *
     * @param channel 发送渠道
     * @param state   点位状态码
     * @return 点位描述
     */
    public static String getDescriptionByState(Integer channel, Integer state) {
        return EnumsUtils.getDescription(state, AnchorState.class);
    }
}
