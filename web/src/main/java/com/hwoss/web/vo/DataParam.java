package com.hwoss.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Hwoss
 * @date 2024/05/28
 * 链路追踪的请求参数
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataParam {


    /**
     * 查看消息Id的链路信息
     */
    private String messageId;

    /**
     * 查看用户的链路信息
     */
    private String receiver;


    /**
     * 业务Id(数据追踪使用)
     * 生成逻辑参考 TaskInfoUtils
     * 如果传入的是模板ID，则生成当天的业务ID
     */
    private String businessId;


    /**
     * 日期时间(检索短信的条件使用)
     */
    private Long dateTime;


}
