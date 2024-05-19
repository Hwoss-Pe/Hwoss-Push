package com.hwoss.handler.deduplication.limit;

import cn.hutool.core.util.IdUtil;
import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.service.AbstractDeduplicationService;
import com.hwoss.suport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Hwoss
 * @date 2024/05/19
 * * 滑动窗口去重器（内容去重采用基于redis中zset的滑动窗口去重，可以做到严格控制单位时间内的频次。）
 * * 业务逻辑：5分钟内相同用户如果收到相同的内容，则应该被过滤掉
 * * 技术方案：由lua脚本实现
 */
@Service(value = "SlideWindowLimitService")
public class SlideWindowLimitService extends AbstractLimitService {
    private static final String LIMIT_TAG = "SW_";

    @Autowired
    private RedisUtils redisUtils;


    private DefaultRedisScript<Long> redisScript;

    //        读入脚本
    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }


    //--KEYS[1]: 限流 key
//--ARGV[1]: 限流窗口,毫秒
//--ARGV[2]: 当前时间戳（作为score）
//--ARGV[3]: 阈值
//--ARGV[4]: score 对应的唯一value
    @Override
    public Set<String> filter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {
        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceivers().size());
        //时间差异也只是几毫秒，对于大多数限流和去重逻辑来说，这样的小时间差异通常是可以接受的。
        long nowTime = System.currentTimeMillis();
        for (String receiver : taskInfo.getReceivers()) {
            String key = LIMIT_TAG + deduplicationSingleKey(taskInfo, receiver, service);
            String score = Long.toString(nowTime);
            String scoreValue = String.valueOf(IdUtil.getSnowflake().nextId());
            Boolean result = redisUtils.execLimitLua(redisScript, Collections.singletonList(key),
                                                     String.valueOf(param.getDeduplicationTime() * 1000),
                                                     score, param.getCountNum().toString(), scoreValue);
//            判断当前接收者是否达到了限流条件
            if (result.equals(Boolean.TRUE)) {
                filterReceiver.add(receiver);
            }

        }
        return filterReceiver;
    }

}
