package com.hwoss.handler.deduplication.limit;

import cn.hutool.core.collection.CollUtil;
import com.common.constant.CommonConstant;
import com.common.domain.TaskInfo;
import com.hwoss.handler.deduplication.DeduplicationParam;
import com.hwoss.handler.deduplication.service.AbstractDeduplicationService;
import com.hwoss.suport.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hwoss
 * @date 2024/05/19
 * 查看一天内是否收到相同渠道的发送5次由pipeline set & mget实现
 */
@Service(value = "SimpleLimitService")
public class SimpleLimitService extends AbstractLimitService {
    private static final String LIMIT_TAG = "SP_";

    @Autowired
    private RedisUtils redisUtils;

    /**
     * @param service
     * @param taskInfo
     * @param param
     * @return {@link Set }<{@link String }>
     * 对接受者进行过滤，返回的是需要过滤的接收者
     */
    @Override
    public Set<String> filter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {
        //存储限流的接收者
        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceivers().size());
        //存储需要更新到redis的接受者
        Map<String, String> readyPutRedisReceiver = new HashMap<>(taskInfo.getReceivers().size());
//      获取所有接受的key，并且加上前缀
        List<String> keys = deduplicationAllKey(service, taskInfo).stream().map(key -> LIMIT_TAG + key).collect(Collectors.toList());
//         获取上面的key已经在redis里面的数据
        Map<String, String> inRedisValue = redisUtils.mGet(keys);

        for (String receiver : taskInfo.getReceivers()) {
//            遍历每一个接受者的key是否在redis里面，
            String key = LIMIT_TAG + deduplicationSingleKey(taskInfo, receiver, service);
            String value = inRedisValue.get(key);
            if (Objects.nonNull(value) && Integer.parseInt(value) >= param.getCountNum()) {
                //如果次数大于规定
                filterReceiver.add(receiver);
            } else {
                readyPutRedisReceiver.put(receiver, key);
            }
        }
        putInRedis(readyPutRedisReceiver, param.getDeduplicationTime(), inRedisValue);

        return filterReceiver;
    }


    /**
     * 进行更新redis里面的数据
     */
    public void putInRedis(Map<String, String> readyPutRedisReceiver, Long deduplicationTime, Map<String, String> inRedisValue) {
        Map<String, String> map = new HashMap<>(readyPutRedisReceiver.size());

        for (Map.Entry<String, String> entry : readyPutRedisReceiver.entrySet()) {
            String key = entry.getKey();
            if (Objects.nonNull(inRedisValue.get(key))) {
                map.put(key, String.valueOf(Integer.parseInt(inRedisValue.get(key)) + 1));
            } else {
                map.put(key, CommonConstant.TRUE.toString());
            }
        }
        if (!CollUtil.isEmpty(map)) {
            redisUtils.pipeLinSetEx(map, deduplicationTime);
        }
    }
}
