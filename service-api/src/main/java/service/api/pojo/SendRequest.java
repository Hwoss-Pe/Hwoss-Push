package service.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 请求信息的对象封装，这里主要封装的是对信息类型
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Accessors(chain = true)
public class SendRequest {
    /**
     * 这个对请求类型的封装，send表示发送，recall就是撤回信息的内容
     */
    private String code;

    /**
     * 模板的Id
     */
    private String messageTemplateId;

    /**
     * 如果是send就必须传入消息对象
     */
    private MessageParam messageParam;

    /**
     * 如果是recall对撤回消息的id多个进行传入messageId
     */
    private List<String> recallList;

    public SendRequest(String code, String messageTemplateId, MessageParam messageParam) {
        this.code = code;
        this.messageTemplateId = messageTemplateId;
        this.messageParam = messageParam;
    }
}
