package com.wxy.service;

import redis.clients.jedis.ShardedJedis;

public interface RedisPool {

    // 获取jedis连接
    ShardedJedis getShardedJedis();

    // 关闭ShardedJedis连接
    void close(ShardedJedis shardedJedisd);
}
