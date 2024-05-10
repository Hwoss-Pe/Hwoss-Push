package com.common.pipeline;

import com.common.enums.RespStatusEnum;

import java.util.Objects;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 自定义异常方便做返回结果
 */
public class ProcessException extends RuntimeException {
    /**
     * 流程处理上下文
     */
    private final ProcessContext<? extends ProcessModel> processContext;


    public <T extends ProcessModel> ProcessException(ProcessContext<T> processContext) {
        super();
        this.processContext = processContext;
    }

    public <T extends ProcessModel> ProcessContext<? extends ProcessModel> getProcessContext() {
        return processContext;
    }

    @Override
    public String getMessage() {

        if (Objects.nonNull(this.processContext)) {
            return this.processContext.getResponse().getMsg();
        }
        return RespStatusEnum.CONTEXT_IS_NULL.getMsg();
    }
}
