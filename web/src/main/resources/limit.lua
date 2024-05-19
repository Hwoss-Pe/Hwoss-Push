--判断是否接收者达到五分钟发送的信息达到两次一样的
--KEYS[1]: 限流 key
--ARGV[1]: 限流窗口,毫秒
--ARGV[2]: 当前时间戳（作为score）
--ARGV[3]: 阈值
--ARGV[4]: score 对应的唯一value



--  移除开始时间窗口之前的数据，维护当前删除0~now-5的数据，维护5分钟数据
redis.call("zremrangeByScore", KEYS[1], 0, ARGV[2] - ARGV[1]);
--统计5分钟内用户收到了几条信息
local res = redis.call("zard", KEYS[1])
--判断阈值,如果没达到条件就把当前数据加进去，并且设置过时
if (res == nil) or (res < tonumber(ARGV[3])) then
    redis.call("zadd", KEYS[1], ARGV[2], ARGV[4])
    redis.call("expire", KEYS[1], ARGV[1] / 1000)
    --设置过期后也要访问才生效，因此进来的时候要进行一个删除操作
    return 0
else
    return 1
end