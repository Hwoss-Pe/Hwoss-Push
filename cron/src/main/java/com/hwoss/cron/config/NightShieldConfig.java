package com.hwoss.cron.config;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.common.constant.CommonConstant;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import com.hwoss.cron.xxl.constant.XxlJobConstant;
import com.hwoss.cron.xxl.enums.ExecutorRouteStrategyEnum;
import com.hwoss.cron.xxl.enums.GlueTypeEnum;
import com.hwoss.cron.xxl.enums.MisfireStrategyEnum;
import com.hwoss.cron.xxl.enums.ScheduleTypeEnum;
import com.hwoss.cron.xxl.pojo.XxlJobInfo;
import com.hwoss.cron.xxl.service.CronTaskService;
import com.hwoss.cron.xxl.utils.XxlJobUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
@Slf4j
public class NightShieldConfig {

    @Value("${xxl.job.admin.username}")
    private String xxlUserName;

    @Value("${xxl.job.admin.password}")
    private String xxlPassword;

    @Value("${xxl.job.admin.addresses}")
    private String xxlAddresses;

    @Autowired
    private XxlJobUtils xxlJobUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CronTaskService cronTaskService;
    /**
     * 默认早上8点之前是凌晨
     */
    private static final String NIGHT = "0 0 8 * * ?";

    @PostConstruct
    public void init() {
        int i = Integer.parseInt(cronTaskService.getGroupId("hwoss", "hwossJob").getData().toString());

//
        if (cronTaskService.isCreated(i, 0, "NightShield", "hwossJob", "admin") || cronTaskService.isCreated(i, 1, "NightShield", "hwossJob", "admin")) {
            return;
        }
        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(i).jobDesc("NightShield")
                .author("admin")
                .scheduleConf(NIGHT)
                .scheduleType(ScheduleTypeEnum.CRON.name())
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name())
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name())
                .executorHandler(XxlJobConstant.JOB_HANDLER_NAME)
                .executorParam("NightShield")
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name())
                .executorTimeout(XxlJobConstant.TIME_OUT)
                .executorFailRetryCount(XxlJobConstant.RETRY_COUNT)
                .glueType(GlueTypeEnum.BEAN.name())
                .triggerStatus(CommonConstant.FALSE)
                .glueRemark(CharSequenceUtil.EMPTY)
                .glueSource(CharSequenceUtil.EMPTY)
                .alarmEmail(CharSequenceUtil.EMPTY)
                .childJobId(CharSequenceUtil.EMPTY).build();


        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
        String path = xxlAddresses + XxlJobConstant.INSERT_URL;
        ReturnT returnT = null;
        HttpResponse response;
        try {
            response = HttpRequest.post(path).form(params).cookie(cronTaskService.getCookie()).execute();
            returnT = JSON.parseObject(response.body(), ReturnT.class);
            if (response.isOk() && returnT.getCode() == ReturnT.SUCCESS_CODE) {
                System.out.println("初始化夜间屏蔽处理器成功");
            }
        } catch (Exception e) {
            log.error("NightShieldConfig#init fail,e:{},param:{},response:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(xxlJobInfo), JSON.toJSONString(returnT));
        }
        cronTaskService.invalidateCookie();
    }


}
