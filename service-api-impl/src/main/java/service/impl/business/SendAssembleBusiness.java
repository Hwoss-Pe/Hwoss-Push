package service.impl.business;

import com.common.constant.CommonConstant;
import com.common.domain.TaskInfo;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import com.hwoss.suport.dao.MessageTemplateDao;
import com.hwoss.suport.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.api.pojo.MessageParam;
import service.impl.domain.SendTaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Hwoss
 * @date 2024/05/11
 * 组装对应的参数
 */
@Slf4j
@Service
public class SendAssembleBusiness implements BusinessProcess<SendTaskModel> {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    private static final String LINK_NAME = "url";

    /**
     * @param context 由于信息渠道的不同，需要拥有一个Content来进行维护
     */
    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();

        //这里用jdk8后的Optional来隐式的对可能为null进行操作，衍生出的方法采用lambda表达式进行解决
        Optional<MessageTemplate> messageTemplate = messageTemplateDao.findById(messageTemplateId);
        if (!messageTemplate.isPresent() || messageTemplate.get().getIsDeleted().equals(CommonConstant.FALSE)) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.TEMPLATE_NOT_FOUND));
            return;
        }
//            获取组装后的任务列表

    }

    private List<TaskInfo> assembleTaskInfoList(SendTaskModel sendTaskModel) {
        List<TaskInfo> taskInfoList = new ArrayList<>();
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        for (MessageParam messageParam : messageParamList) {
            TaskInfo taskInfo = TaskInfo.builder()
                    .build();
        }
        return taskInfoList;
    }
}
