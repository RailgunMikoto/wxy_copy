package com.wxy.service.impl;

import com.wxy.service.RedisPool;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

@Service
public class RedisPoolImpl implements RedisPool {

    @Resource(name = "shardedJedisPool")
    private ShardedJedisPool pool;


    /**
     * 获取连接
     * @return
     */
    @Override
    public ShardedJedis getShardedJedis() {
        return pool.getResource();
    }

    // 关闭连接
    @Override
    public void close(ShardedJedis shardedJedisd) {
        if(shardedJedisd != null){
            shardedJedisd.close();
        }
    }
}
