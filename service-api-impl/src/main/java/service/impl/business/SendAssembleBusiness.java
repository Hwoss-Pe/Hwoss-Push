package service.impl.business;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.constant.CommonConstant;
import com.common.domain.TaskInfo;
import com.common.dto.model.ContentModel;
import com.common.enums.ChannelType;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import com.hwoss.suport.dao.MessageTemplateDao;
import com.hwoss.suport.domain.MessageTemplate;
import com.hwoss.suport.utils.ContentHolderUtil;
import com.hwoss.suport.utils.TaskInfoUtils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.common.reflection.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.api.pojo.MessageParam;
import service.impl.domain.SendTaskModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Hwoss
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
        assembleTaskInfoList(sendTaskModel, messageTemplate.get());

    }

    private List<TaskInfo> assembleTaskInfoList(SendTaskModel sendTaskModel, MessageTemplate messageTemplate) {
        List<TaskInfo> taskInfoList = new ArrayList<>();
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        for (MessageParam messageParam : messageParamList) {
            TaskInfo taskInfo = TaskInfo.builder()
                    .businessId(TaskInfoUtils.generateBusinessId(messageTemplate.getId(), messageTemplate.getTemplateType()))
                    .bizId(messageParam.getBidId())
                    .messageTemplateId(messageTemplate.getId())
                    .messageId(TaskInfoUtils.generateMessageId())
                    .receivers(new HashSet<>(Arrays.asList(messageParam.getReceiver().split(StrPool.COMMA))))
                    .sendAccount(messageTemplate.getSendAccount())
                    .sendChannel(messageTemplate.getSendChannel())
                    .idType(messageTemplate.getIdType())
                    .shieldType(messageTemplate.getShieldType())
                    .templateType(messageTemplate.getTemplateType())
                    .msgType(messageTemplate.getMsgType())
                    .contentModel(getContentModel(messageTemplate, messageParam))
                    .build();
        }
        return taskInfoList;
    }

    /**
     * @return {@link ContentModel }
     * 组装参数，把占位符组装后返回给TaskInfo去执行
     */
    public ContentModel getContentModel(MessageTemplate messageTemplate, MessageParam messageParam) {
//       获取对应渠道的消息类型模板
        Integer sendChannel = messageTemplate.getSendChannel();
        Class<? extends ContentModel> contentModelClass = ChannelType.getContentModelClassByCode(sendChannel);
        // 得到模板的 msgContent 和 入参
        Map<String, String> variables = messageParam.getVariables();
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent());

//        组装通过携带去覆盖模板变量的值
        Field[] fields = ReflectUtil.getFields(contentModelClass);
        /*
        这里如果不用huTool包去做反射异常校验的话，自己写代码会需要大量tryCatch
         */
        ContentModel contentModel = ReflectUtil.newInstance(contentModelClass);
        for (Field field : fields) {
            String origin = jsonObject.getString(field.getName());
//            进行替换，把反射对象设置对应的内容
            if (!origin.isEmpty()) {
                String resultValue = ContentHolderUtil.replacePlaceholder(origin, variables);
                //获得后如果是bean对象就进行转换成bean，如果不是保持原来的字符串
                Object resultObj = JSONUtil.isJsonObj(resultValue)
                        ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;

            }

        }
//        如果存在url进行拼装url方便后续追踪
        String url = (String) ReflectUtil.getFieldValue(contentModel, LINK_NAME);
        if (CharSequenceUtil.isNotBlank(url)) {
            String resultUrl = TaskInfoUtils.generateUrl(url, messageTemplate.getId(), messageTemplate.getTemplateType());
            ReflectUtil.setFieldValue(contentModel, LINK_NAME, resultUrl);
        }
        return contentModel;
    }
}
