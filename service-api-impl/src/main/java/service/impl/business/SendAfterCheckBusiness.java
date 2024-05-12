package service.impl.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.common.domain.TaskInfo;
import com.common.enums.IdType;
import com.common.enums.RespStatusEnum;
import com.common.pipeline.BusinessProcess;
import com.common.pipeline.ProcessContext;
import com.common.vo.BasicResultVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import service.impl.domain.SendTaskModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hwoss
 * @date 2024/05/12
 * 对接收者的合法校验
 */
@Service
@Slf4j
public class SendAfterCheckBusiness implements BusinessProcess<SendTaskModel> {
    //正则表达式
    public static final String PHONE_REGEX_EXP = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$";
    public static final String EMAIL_REGEX_EXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    /**
     * 邮件和手机号正则
     */
    protected static final Map<Integer, String> CHANNEL_REGEX_EXP = new HashMap<>();

    static {
        CHANNEL_REGEX_EXP.put(IdType.PHONE.getCode(), PHONE_REGEX_EXP);
        CHANNEL_REGEX_EXP.put(IdType.EMAIL.getCode(), EMAIL_REGEX_EXP);
    }

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        List<TaskInfo> taskInfoList = sendTaskModel.getTaskInfoList();
//        过滤掉不合法的手机号
        filterIllegalReceiver(taskInfoList);
        if (CollUtil.isEmpty(taskInfoList)) {
            context.setIsBreak(true).setResponse(BasicResultVo.fail(RespStatusEnum.CLIENT_BAD_PARAMETER, "手机号或邮箱不合法, 无有效的发送任务"));
        }
    }

    public void filterIllegalReceiver(List<TaskInfo> taskInfoList) {
        TaskInfo first = CollUtil.getFirst(taskInfoList.iterator());
        Integer idType = first.getIdType();
        String EXP = CHANNEL_REGEX_EXP.get(idType);
        filter(taskInfoList, EXP);
    }

    public void filter(List<TaskInfo> taskInfoList, String regex) {
        for (TaskInfo taskInfo : taskInfoList) {
            Set<String> collect = taskInfo.getReceivers().stream()
                    .filter(phone -> ReUtil.isMatch(regex, phone))
                    .collect(Collectors.toSet());
            if (!CollUtil.isEmpty(collect)) {
                taskInfo.getReceivers().retainAll(collect);
                log.error("messageTemplateId:{} find illegal receiver!{}", taskInfo.getMessageTemplateId(), JSON.toJSONString(collect));
            }
        }
    }
}
