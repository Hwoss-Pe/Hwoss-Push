package com.hwoss.utils;

import com.hwoss.callback.RedisPipelineCallBack;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import com.hwoss.config.FlinkConstant;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Hwoss
 * @date 2024/05/27
 * 无Spring环境下使用Redis，基于Lettuce封装
 */
public class LettuceRedisUtils {

    /**
     * 初始化 redisClient
     */
    private static final RedisClient redisClient;

    static {
        RedisURI redisUri = RedisURI.Builder.redis(FlinkConstant.REDIS_IP)
                .withPort(Integer.parseInt(FlinkConstant.REDIS_PORT))
                .build();
        redisClient = RedisClient.create(redisUri);
    }

    private LettuceRedisUtils() {

    }


    /**
     * 封装一下pipeline
     */
    public static void pipeline(RedisPipelineCallBack pipelineCallBack) {
//        获取链接，用字节流传输，并且里面封装了同步和异步
        StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(new ByteArrayCodec());

//        获取异步的状态链接接口
        RedisAsyncCommands<byte[], byte[]> commands = connect.async();
//      执行管道异步回调
        List<RedisFuture<?>> invoke = pipelineCallBack.invoke(commands);
//      刷新缓冲管道命令确保发送
        commands.flushCommands();

//        等待回调，全部回调，由限制时间
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS, invoke.toArray(new RedisFuture[(invoke.size())]));

//        关闭链接
        connect.close();

    }
}
