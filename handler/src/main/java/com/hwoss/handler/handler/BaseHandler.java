package com.hwoss.handler.handler;

import com.common.domain.AnchorInfo;
import com.common.domain.TaskInfo;
import com.common.enums.AnchorState;
import com.hwoss.handler.flowcontrol.FlowControlFactory;
import com.hwoss.handler.flowcontrol.FlowControlParam;
import com.hwoss.suport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class BaseHandler implements Handler {
    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer channelCode;
    /**
     * 限流相关的参数
     * 子类初始化的时候指定
     */
    protected FlowControlParam flowControlParam;
    @Autowired
    private HandlerChannelHolder handlerHolder;
    @Autowired
    private LogUtils logUtils;
    @Autowired
    private FlowControlFactory flowControlFactory;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 初始化渠道与Handler的映射关系
     */
    @PostConstruct
    private void init() {
        handlerHolder.putHandler(channelCode, this);
    }

    /**
     * 将撤回的消息存储到redis
     *
     * @param prefix            redis前缀
     * @param messageTemplateId 消息模板id
     * @param taskId            消息下发taskId
     * @param expireTime        存储到redis的有效时间（跟对应渠道可撤回多久的消息有关系)
     *                          相等于维护了messageId和对应task的关系在redis存储
     */
    protected void saveRecallInfo(String prefix, Long messageTemplateId, String taskId, Long expireTime) {
//        模板id，多个taskId
        redisTemplate.opsForList().leftPush(prefix + messageTemplateId, taskId);
//        对应的TaskId
        redisTemplate.opsForValue().set(prefix + taskId, taskId);
//        设置模板Id的过期时间
        redisTemplate.expire(prefix + messageTemplateId, expireTime, TimeUnit.SECONDS);
//        设置taskId的过期时间
        redisTemplate.expire(prefix + taskId, expireTime, TimeUnit.SECONDS);
    }


    @Override
    public void doHandler(TaskInfo taskInfo) {
//        判断之类是否有指定限流规则
        if (Objects.nonNull(flowControlParam)) {
            flowControlFactory.flowControl(taskInfo, flowControlParam);
        }
        if (handler(taskInfo)) {
            logUtils.print(AnchorInfo.builder()
                                   .state(AnchorState.SEND_SUCCESS.getCode())
                                   .bizId(taskInfo.getBizId())
                                   .messageId(taskInfo.getMessageId())
                                   .businessId(taskInfo.getBusinessId())
                                   .ids(taskInfo.getReceivers())
                                   .build());
            return;
        }
        logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_FAIL.getCode())
                               .bizId(taskInfo.getBizId()).
                        messageId(taskInfo.getMessageId()).
                        businessId(taskInfo.getBusinessId()).
                        ids(taskInfo.getReceivers())
                               .build());
    }

    /**
     * 统一处理的handler接口
     *
     * @param taskInfo
     * @return
     */
    public abstract boolean handler(TaskInfo taskInfo);

}
