package com.wxy.service.impl;

import com.wxy.beans.CacheKeyPrefix;
import com.wxy.service.JedisService;
import com.wxy.service.RedisPool;
import com.wxy.util.CacheKeyUtil;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
public class JedisServiceImpl implements JedisService {

    @Resource
    RedisPool redisPool;

    /**
     * 缓存数据
     * @param key
     * @param value
     * @param timeout
     * @param prefix
     */
    @Override
    public void saveCache(String key, String value, int timeout, CacheKeyPrefix prefix) {

        ShardedJedis shardedJedis = null;
        try{
            // 获取连接
            shardedJedis = redisPool.getShardedJedis();
            // 存入数据
            shardedJedis.setex(CacheKeyUtil.getCacheKey(key, prefix), timeout, value);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            redisPool.close(shardedJedis);
        }
    }

    /**
     * 取出数据
     * @param key
     * @param prefix
     * @return
     */
    @Override
    public String getCache(String key, CacheKeyPrefix prefix) {
        ShardedJedis shardedJedis = null;
        String value = null;
        try{
            shardedJedis = redisPool.getShardedJedis();
            value = shardedJedis.get(CacheKeyUtil.getCacheKey(key, prefix));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            redisPool.close(shardedJedis);
        }

        return value;
    }
}
