package com.hwoss.web.service;

import com.common.vo.BasicResultVo;
import com.hwoss.suport.domain.MessageTemplate;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessageTemplateService {


    /**
     * 统计未删除的条数
     *
     * @return
     */
    Long count();

    /**
     * 单个 保存或者更新
     * 存在ID 更新
     * 不存在ID保存
     *
     * @param messageTemplate
     * @return
     */
    MessageTemplate saveOrUpdate(MessageTemplate messageTemplate);


    /**
     * 软删除(deleted=1)
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID查询模板信息
     *
     * @param id
     * @return
     */
    MessageTemplate queryById(Long id);


    /**
     * 启动模板的定时任务
     *
     * @param id
     * @return
     */
    BasicResultVo startCronTask(Long id);

    /**
     * 暂停模板的定时任务
     *
     * @param id
     * @return
     */
    BasicResultVo stopCronTask(Long id);

}
