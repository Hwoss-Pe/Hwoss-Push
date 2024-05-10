package service.api.service;

import service.api.pojo.SendRequest;
import service.api.pojo.SendResponse;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 撤回消息
 */
public interface RecallService {
    /**
     * 根据 模板ID 或消息id 撤回消息
     * 如果只传入 messageTemplateId，则会撤回整个模板下发的消息
     * 如果还传入 recallMessageId，则优先撤回该 ids 的消息
     *
     * @param request
     * @return
     */
    SendResponse recall(SendRequest request);
}
