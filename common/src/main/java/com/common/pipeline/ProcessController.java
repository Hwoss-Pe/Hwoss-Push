package com.common.pipeline;

import com.common.enums.RespStatusEnum;
import com.common.vo.BasicResultVo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 流程执行
 */
public class ProcessController {
    private Map<String, ProcessTemplate<?>> templateConfig = null;

    public ProcessContext process(ProcessContext context) {
//    前置检查
        try {
            preCheck(context);
        } catch (ProcessException e) {
            return e.getProcessContext();
        }
//遍历节点流程
        List<? extends BusinessProcess<?>> businessProcessList = templateConfig.get(context.getCode()).getBusinessProcessList();
        for (BusinessProcess<?> businessProcess : businessProcessList) {
            businessProcess.process(context);
//            通过设置break来结束责任链的运行
            if (Boolean.TRUE.equals(context.getIsBreak())) {
                break;
            }
        }
        return context;
    }


    /**
     * @param context 预检查
     */
    public <T extends ProcessModel> void preCheck(ProcessContext<T> context) {
//        上下文检查
        if (Objects.isNull(context)) {
            context = new ProcessContext<>();
            throw new ProcessException(context);
        }
//        业务代码
        if (Objects.isNull(context.getCode())) {
            context.setResponse(BasicResultVo.fail(RespStatusEnum.BUSINESS_CODE_IS_NULL));
            throw new ProcessException(context);
        }
//        模板
        ProcessTemplate processTemplates = templateConfig.get(context.getCode());
        if (Objects.isNull(processTemplates)) {
            context.setResponse(BasicResultVo.fail(RespStatusEnum.PROCESS_TEMPLATE_IS_NULL));
            throw new ProcessException(context);
        }
//        模板执行的列表
        List<BusinessProcess> businessProcessList = processTemplates.getBusinessProcessList();
        if (Objects.isNull(businessProcessList) || businessProcessList.isEmpty()) {
            context.setResponse(BasicResultVo.fail(RespStatusEnum.PROCESS_LIST_IS_NULL));
            throw new ProcessException(context);
        }
    }
}
