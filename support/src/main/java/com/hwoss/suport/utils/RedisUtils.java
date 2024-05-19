package com.hwoss.suport.utils;


import cn.hutool.core.collection.CollUtil;
import com.common.constant.CommonConstant;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class RedisUtils {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param list
     * @return {@link Map }<{@link String }, {@link String }>
     * 把list查完映射成map
     **/

    public Map<String, String> mGet(List<String> list) {
        HashMap<String, String> map = new HashMap<>();
        List<String> value = redisTemplate.opsForValue().multiGet(list);
        try {
            if (!CollUtil.isEmpty(value)) {
                for (int i = 0; i < list.size(); i++) {
                    if (Objects.nonNull(value.get(i))) {
                        map.put(list.get(i), value.get(i));
                    }
                }
            }
        } catch (Exception e) {
            log.error("RedisUtils#mGet failed,e:{}", Throwables.getStackTraceAsString(e));
        }
        return map;
    }

    /**
     * @param key
     * @return {@link Map }<{@link Object },{@link Object }>
     * 根据key获取哈希表
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("RedisUtils#hGetAll failed,e:{}", Throwables.getStackTraceAsString(e));
        }
        return Collections.emptyMap();
    }

    /**
     * lRange
     *
     * @param key
     */
    public List<String> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtils#lRange fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return new ArrayList<>();
    }

    /**
     * lLen 方法
     */
    public Long lLen(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("RedisUtils#lLen fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return 0L;
    }

    /**
     * lpush 方法 并指定 过期时间
     */
    public void lPush(String key, String value, Long seconds) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                connection.lPush(key.getBytes(), value.getBytes());
                connection.expire(key.getBytes(), seconds);
                return null;
            });
        } catch (Exception e) {
            log.error("RedisUtils#lPush fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * lPop 方法
     */
    public String lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("RedisUtils#lPop fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return "";
    }

    public void pipeLinSetEx(Map<String, String> map, Long seconds) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    connection.setEx(entry.getKey().getBytes(), seconds, entry.getValue().getBytes());
                }
                //            这里的用来redis 的pipeLine进行多命令缓存发送，然后包装RedisCallback需要返<T>，以便他能发送
                return null;
            });
        } catch (Exception e) {
            log.error("RedisUtils#pipelineSetEx fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * pipeline 对某个哈希表中的字段值进行增量操作，并同时设置该哈希表的过期时间。
     *
     * @param seconds 过期时间
     * @param delta   自增的步长
     */
    public void pipelineHashIncrByEx(Map<String, String> keyValues, Long seconds, Long delta) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    connection.hIncrBy(entry.getKey().getBytes(), entry.getValue().getBytes(), delta);
//                    给redis里面对应key的哈希类的对应字段增加delta
                    connection.expire(entry.getKey().getBytes(), seconds);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("redis pipelineSetEX fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * @param redisScript
     * @param keys
     * @param args
     * @return {@link Boolean }
     * 传入对应的脚本，以及list参数可变参数到脚本里面，返回值是脚本的返回值
     */
    public Boolean execLimitLua(RedisScript<Long> redisScript, List<String> keys, String... args) {

        try {
            Long execute = redisTemplate.execute(redisScript, keys, args);
            if (Objects.isNull(execute)) {
                return false;
            }
            return CommonConstant.TRUE.equals(execute.intValue());
        } catch (Exception e) {
            log.error("redis execLimitLua fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return false;
    }
}
