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
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
@Builder
public class BatchRequest {
    /**
     * 和request一样的
     */
    private String code;

    private Long MessageTemplateId;

    /**
     * 存储多个消息内容
     */
    private List<MessageParam> messageParamList;
}
