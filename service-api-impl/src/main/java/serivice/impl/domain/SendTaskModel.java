package serivice.impl.domain;

import com.common.domain.TaskInfo;
import com.common.pipeline.ProcessModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.api.pojo.MessageParam;

import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 发送消息的模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaskModel implements ProcessModel {
    /**
     * 这里也要做模板Id
     */
    private Long MessageTemplateId;

    /**
     * 消息请求参数
     */
    private List<MessageParam> messageParamList;

    /**
     * 发送任务的信息
     */
    private List<TaskInfo> taskInfo;
}
