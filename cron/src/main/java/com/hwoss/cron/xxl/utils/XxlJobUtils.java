package com.hwoss.cron.xxl.utils;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.common.constant.CommonConstant;
import com.common.enums.RespStatusEnum;
import com.common.vo.BasicResultVo;
import com.hwoss.cron.xxl.constant.XxlJobConstant;
import com.hwoss.cron.xxl.enums.MisfireStrategyEnum;
import com.hwoss.cron.xxl.enums.ScheduleTypeEnum;
import com.hwoss.cron.xxl.pojo.XxlJobGroup;
import com.hwoss.cron.xxl.pojo.XxlJobInfo;
import com.hwoss.cron.xxl.service.CronTaskService;
import com.hwoss.cron.xxl.enums.*;
import com.hwoss.suport.domain.MessageTemplate;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.Objects;

@Component
public class XxlJobUtils {
    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.executor.jobHandlerName}")
    private String jobHandlerName;

    @Autowired
    private CronTaskService cronTaskService;


    public XxlJobUtils() {
    }

    public XxlJobInfo getJobInfo(MessageTemplate messageTemplate) {
        String cron = messageTemplate.getExpectPushTime();
        if (cron.equals(CommonConstant.FALSE.toString())) {
//            如果没有配置cron就是直接发送的，获得一个cron的当前时间偏移十秒后的cron表达式"ss mm HH dd MM ? yyyy-yyyy
            cron = DateUtil.format(DateUtil.offsetSecond(new Date(), XxlJobConstant.DELAY_TIME), CommonConstant.CRON_FORMAT);
        }
        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(queryJobGroupId()).jobDesc(messageTemplate.getName())
                .author(messageTemplate.getCreator())
                .scheduleConf(cron)
                .scheduleType(ScheduleTypeEnum.CRON.name())
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name())
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name())
//                执行器配置相关，这里把参数设置成对应模板的id
                .executorHandler(XxlJobConstant.JOB_HANDLER_NAME)
                .executorParam(String.valueOf(messageTemplate.getId()))
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name())
                .executorTimeout(XxlJobConstant.TIME_OUT)
                .executorFailRetryCount(XxlJobConstant.RETRY_COUNT)
//                工作类型相关
                .glueType(GlueTypeEnum.BEAN.name())
                .triggerStatus(CommonConstant.FALSE)
                .glueRemark(CharSequenceUtil.EMPTY)
                .glueSource(CharSequenceUtil.EMPTY)
                .alarmEmail(CharSequenceUtil.EMPTY)
                .childJobId(CharSequenceUtil.EMPTY).build();

        if (Objects.nonNull(messageTemplate.getCronTaskId())) {
            xxlJobInfo.setId(messageTemplate.getCronTaskId());
        }
        return xxlJobInfo;
    }

    /**
     * @return {@link Integer }
     * 根据就配置文件的内容获取jobGroupId，没有则创建
     */
    public Integer queryJobGroupId() {
        BasicResultVo<Integer> groupId = cronTaskService.getGroupId(appName, jobHandlerName);
        Integer id = groupId.getData();
        if (Objects.isNull(id)) {
            XxlJobGroup xxlJobGroup = XxlJobGroup.builder()
                    .appname(appName)
                    .title(jobHandlerName)
                    .addressType(CommonConstant.FALSE).build();
            if (RespStatusEnum.SUCCESS.getCode().equals(cronTaskService.createGroup(xxlJobGroup).getStatus())) {
                return (int) cronTaskService.getGroupId(appName, jobHandlerName).getData();
            }
        }
        return id;
    }
}
