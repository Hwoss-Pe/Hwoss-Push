package service.api.service;

import service.api.pojo.BatchRequest;
import service.api.pojo.SendRequest;
import service.api.pojo.SendResponse;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 发送接口
 */
public interface SendService {
    /**
     * @param request eg:    {"code":"send","messageParam":{"bizId":null,"extra":null,"receiver":"123@qq.com","variables":null},"messageTemplateId":17,"recallMessageId":null}
     * @return SendResponse eg:    {"code":"0","data":[{"bizId":"ecZim2-FzdejNSY-sqgCM","businessId":2000001720230815,"messageId":"ecZim2-FzdejNSY-sqgCM"}],"msg":"操作成功"}
     * 单信息发送
     */
    SendResponse send(SendRequest request);

    /**
     * @param request
     * @return {@link SendResponse }
     * 多信息发送
     */
    SendResponse batchSend(BatchRequest request);

}
