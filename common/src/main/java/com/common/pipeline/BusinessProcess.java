package com.common.pipeline;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 这个接口可以去处理相关子类，注意他是业务的
 */
public interface BusinessProcess<T extends ProcessModel> {
    /**
     * @param context 继承这个接口去处理真正的逻辑
     */
    void process(ProcessContext<T> context);

}
