package com.hwoss.cron.xxl.service;

import com.common.vo.BasicResultVo;
import com.hwoss.cron.xxl.pojo.XxlJobGroup;
import com.hwoss.cron.xxl.pojo.XxlJobInfo;

/**
 * @author Hwoss
 * @date 2024/05/24
 * 定时任务
 */
public interface CronTaskService {
    /**
     * 新增/修改 定时任务
     *
     * @param xxlJobInfo
     * @return 新增时返回任务Id，修改时无返回
     */
    BasicResultVo saveCronTask(XxlJobInfo xxlJobInfo);

    /**
     * 删除定时任务
     *
     * @param taskId
     * @return BasicResultVO
     */
    BasicResultVo deleteCronTask(Integer taskId);

    /**
     * 启动定时任务
     *
     * @param taskId
     * @return BasicResultVO
     */
    BasicResultVo startCronTask(Integer taskId);


    /**
     * 暂停定时任务
     *
     * @param taskId
     * @return BasicResultVO
     */
    BasicResultVo stopCronTask(Integer taskId);


    /**
     * 得到执行器Id
     *
     * @param appName
     * @param title
     * @return BasicResultVO
     */
    BasicResultVo getGroupId(String appName, String title);

    /**
     * 创建执行器
     *
     * @param xxlJobGroup
     * @return BasicResultVO
     */
    BasicResultVo createGroup(XxlJobGroup xxlJobGroup);

}
