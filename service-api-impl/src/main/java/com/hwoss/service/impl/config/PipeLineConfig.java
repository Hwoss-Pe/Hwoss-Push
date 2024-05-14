package com.hwoss.service.impl.config;

import com.common.pipeline.ProcessController;
import com.common.pipeline.ProcessTemplate;
import com.hwoss.service.impl.business.recall.RecallAssembleBusiness;
import com.hwoss.service.impl.business.recall.RecallMQBusiness;
import com.hwoss.service.impl.business.send.SendAssembleBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.hwoss.service.impl.business.send.SendAfterCheckBusiness;
import com.hwoss.service.api.enums.BusinessCode;
import com.hwoss.service.impl.business.send.SendMQBusiness;
import com.hwoss.service.impl.business.send.SendPreCheckBusiness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hwoss
 * @date 2024/05/11
 * 这个是对责任链的配置
 */
@ComponentScan(basePackages = "com.hwoss.service.impl.business")
@Configuration
public class PipeLineConfig {


    @Autowired
    private SendPreCheckBusiness sendPreCheckAction;
    @Autowired
    private SendAssembleBusiness sendAssembleAction;
    @Autowired
    private SendAfterCheckBusiness sendAfterCheckAction;
    @Autowired
    private SendMQBusiness sendMQBusiness;
    @Autowired
    private RecallAssembleBusiness recallAssembleBusiness;
    @Autowired
    private RecallMQBusiness recallMQBusiness;

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     *
     * @return
     */
    //    完成发送信息模板
    @Bean("commonSendTemplate")
    public ProcessTemplate commonSendTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
//        设置具体业务
        processTemplate.setBusinessProcessList(Arrays.asList(
                sendPreCheckAction, sendAssembleAction, sendAfterCheckAction, sendMQBusiness
        ));
        return processTemplate;
    }

    @Bean("commonRecallTemplate")
    public ProcessTemplate commonRecallTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
//        设置具体业务
        processTemplate.setBusinessProcessList(Arrays.asList(
                recallAssembleBusiness, recallMQBusiness
        ));
        return processTemplate;
    }

    //    启动的时候注入并且映射里面的mapConfig传入对应的template
    @Bean
    public ProcessController getProcessController() {
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate<?>> templateConfig = new HashMap<>(4);
        templateConfig.put(BusinessCode.SEND.getCode(), commonSendTemplate());
        templateConfig.put(BusinessCode.RECALL.getCode(), commonRecallTemplate());
        processController.setTemplateConfig(templateConfig);
        return processController;
    }
}
