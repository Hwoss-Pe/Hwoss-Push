package service.impl.business;

import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import org.springframework.beans.factory.annotation.Value;
import service.impl.domain.SendTaskModel;

public class SendMQBusiness implements BusinessProcess<SendTaskModel> {

    @Value("${hwoss.business.topic.name}")
    private String sendMessageTopic;

    @Value("${hwoss.business.tagId.value}")
    private String tagId;

    /**
     * @param context 这把信息序列化后传入到mq里面
     */
    @Override
    public void process(ProcessContext<SendTaskModel> context) {

    }
}
