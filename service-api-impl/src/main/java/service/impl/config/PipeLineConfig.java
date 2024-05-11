package service.impl.config;

import com.common.pipeline.ProcessController;
import com.common.pipeline.ProcessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import service.impl.business.SendPreCheckBusiness;
import service.api.enums.BusinessCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hwoss
 * @date 2024/05/11
 * 这个是对责任链的配置
 */
@ComponentScan(basePackages = "service.impl.business")
@Configuration
public class PipeLineConfig {

    @Autowired
    private SendPreCheckBusiness sendPreCheckBusiness;

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
                sendPreCheckBusiness

        ));
        return processTemplate;
    }


    //    启动的时候注入并且映射里面的mapConfig传入对应的template
    @Bean
    public ProcessController getProcessController() {
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate<?>> templateConfig = new HashMap<>(4);
        templateConfig.put(BusinessCode.SEND.getCode(), commonSendTemplate());
//        templateConfig.put(BusinessCode.RECALL.getCode(), recallMessageTemplate);
        processController.setTemplateConfig(templateConfig);
        return processController;
    }
}
