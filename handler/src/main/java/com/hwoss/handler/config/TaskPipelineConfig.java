package com.hwoss.handler.config;

import com.common.pipeline.ProcessController;
import com.common.pipeline.ProcessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hwoss
 * @date 2024/05/16
 * 用于业务层的pipePine，注意自动装配的用bean名字来获取
 * 丢弃
 * 屏蔽
 * 去重
 * 发送
 */
@Configuration
public class TaskPipelineConfig {
    public static final String PIPELINE_HANDLER_CODE = "handler";
//    @Autowired
//    private DiscardAction discardAction;
//    @Autowired
//    private ShieldAction shieldAction;
//    @Autowired
//    private DeduplicationAction deduplicationAction;
//    @Autowired
//    private SendMessageAction sendMessageAction;

    @Bean("taskTemplate")
    public ProcessTemplate taskTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
//        processTemplate.setBusinessProcessList(Arrays.asList(discardAction, shieldAction, deduplicationAction, sendMessageAction));
        return processTemplate;
    }


    @Bean("handlerProcessController")
    public ProcessController processController() {
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate<?>> templateMap = new HashMap<>();
        templateMap.put(PIPELINE_HANDLER_CODE, taskTemplate());
        processController.setTemplateConfig(templateMap);
        return processController;
    }

}
