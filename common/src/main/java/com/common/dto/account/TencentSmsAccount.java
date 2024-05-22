package com.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hwoss
 * @date 2024/05/21
 * 纯api参数的接入，
 * * 腾讯短信参数
 * * <p>
 * * 账号参数示例：
 * * {
 * * "url": "sms.tencentcloudapi.com",
 * * "region": "ap-guangzhou",
 * * "secretId": "AKIDhDxxxxxxxx1WljQq",
 * * "secretKey": "B4hwww39yxxxrrrrgxyi",
 * * "smsSdkAppId": "1423123125",
 * * "templateId": "1182097",
 * * "signName": "公众号",
 * * "supplierId": 10,
 * * "supplierName": "腾讯云",
 * * "scriptName": "TencentSmsScript"
 * * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TencentSmsAccount extends SmsAccount {
    /**
     * api相关
     */
    private String url;
    private String region;

    /**
     * 账号相关
     */
    private String secretId;
    private String secretKey;
    private String smsSdkAppId;
    private String templateId;
    private String signName;
}
