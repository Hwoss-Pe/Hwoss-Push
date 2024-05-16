package com.hwoss.handler.pending;

import com.common.domain.TaskInfo;
import com.common.pipeline.ProcessContext;
import com.common.pipeline.ProcessController;
import com.common.vo.BasicResultVo;
import com.hwoss.handler.config.TaskPipelineConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Accessors(chain = true)
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Task implements Runnable {
    private TaskInfo taskInfo;

    @Autowired
    @Qualifier("handlerProcessController")
    private ProcessController processController;

    //    定义线程池每个线程执行的逻辑
    @Override
    public void run() {
        ProcessContext processContext = ProcessContext.builder()
                .processModel(taskInfo)
                .isBreak(false)
                .Code(TaskPipelineConfig.PIPELINE_HANDLER_CODE)
                .response(BasicResultVo.success())
                .build();
        processController.process(processContext);
    }
}
