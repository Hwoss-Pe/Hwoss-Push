package com.common.enums;

import com.common.dto.model.ContentModel;
import com.common.dto.model.EmailContentModel;
import com.common.dto.model.SmsContentModel;
import lombok.*;

import java.util.Arrays;


@Getter
@ToString
@AllArgsConstructor
public enum ChannelType implements PowerfulEnums {

    /**
     * IM(站内信)  -- 未实现该渠道
     */
//    IM(10, "IM(站内信)", ImContentModel.class, "im.hwoss", null, null),
    /**
     * push(通知栏) --安卓 已接入 个推
     */
//    PUSH(20, "push(通知栏)", PushContentModel.class, "push.hwoss", "ge_tui_access_token_", 3600 * 24L),
    /**
     * sms(短信)  -- 腾讯云、云片
     */
    SMS(30, "sms(短信)", SmsContentModel.class, "sms.hwoss", null, null),
    /**
     * email(邮件) -- QQ、163邮箱
     */
    EMAIL(40, "email(邮件)", EmailContentModel.class, "email.hwoss", null, null),
    /**
     * officialAccounts(微信服务号) --
     * accessToken 交由 weixin-java-mp 组件管理，所以不设置expireTime
     */
//    OFFICIAL_ACCOUNT(50, "officialAccounts(服务号)", OfficialAccountsContentModel.class, "official_accounts.hwoss", "official_account_", null),
    /**
     * miniProgram(微信小程序)
     * accessToken 交由 weixin-java-miniapp 组件管理，所以不设置expireTime
     */
//    MINI_PROGRAM(60, "miniProgram(小程序)", MiniProgramContentModel.class, "mini_program.hwoss", "mini_program_", null),

    /**
     * enterpriseWeChat(企业微信)
     */
//    ENTERPRISE_WE_CHAT(70, "enterpriseWeChat(企业微信)", EnterpriseWeChatContentModel.class, "enterprise_we_chat.hwoss", null, null),
    /**
     * dingDingRobot(钉钉机器人)
     */
//    DING_DING_ROBOT(80, "dingDingRobot(钉钉机器人)", DingDingRobotContentModel.class, "ding_ding_robot.hwoss", null, null),
    /**
     * dingDingWorkNotice(钉钉工作通知)
     */
//    DING_DING_WORK_NOTICE(90, "dingDingWorkNotice(钉钉工作通知)", DingDingWorkContentModel.class, "ding_ding_work_notice.hwoss", "ding_ding_access_token_", 3600 * 2L),
    /**
     * enterpriseWeChat(企业微信机器人)
     */
//    ENTERPRISE_WE_CHAT_ROBOT(100, "enterpriseWeChat(企业微信机器人)", EnterpriseWeChatRobotContentModel.class, "enterprise_we_chat_robot.hwoss", null, null),
    /**
     * feiShuRoot(飞书机器人)
     */
//    FEI_SHU_ROBOT(110, "feiShuRoot(飞书机器人)", FeiShuRobotContentModel.class, "fei_shu_robot.hwoss", null, null),
    /**
     * alipayMiniProgram(支付宝小程序)
     */
//    ALIPAY_MINI_PROGRAM(120, "alipayMiniProgram(支付宝小程序)", AlipayMiniProgramContentModel.class, "alipay_mini_program.hwoss", null, null),
    ;


    /**
     * 编码值
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 让对应的类继承这个接口，然后书写属于自己的格式
     */
    private Class<? extends ContentModel> contentModelClass;
    /**
     * 作为中间件的标识
     */
    private final String codeEn;


    /**
     * accessToken prefix
     */
    private final String accessTokenPrefix;

    /**
     * accessToken expire
     * 单位秒
     */
    private final Long accessTokenExpire;


    /**
     * @return {@link String }
     * 如果命名对这里甚至不用写，交给@Data
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Integer Code() {
        return this.code;
    }

    /**
     * @param code
     * @return {@link Class }<{@link ? } {@link extends } {@link ContentModel }>
     * 通过输出流的方式去通过code用values获取对应枚举数组转化成流然后过滤，然后用map映射成对应的类，然后返回第一个,否则为null
     */
    public static Class<? extends ContentModel> getContentModelClassByCode(Integer code) {
        ChannelType[] values = values();
        return (Class<? extends ContentModel>) Arrays.stream(values).filter(value -> value.getCode().equals(code))
                .map(ChannelType::getContentModelClass)
                .findFirst().orElse(null);
    }
}
