package com.wxy.service;

import com.wxy.beans.CacheKeyPrefix;

public interface JedisService {

    // 保存数据
    void saveCache(String key, String value, int timeout, CacheKeyPrefix prefix);


    // 取出数据
    String getCache(String key, CacheKeyPrefix prefix);
}
