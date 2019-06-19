package com.wxy.util;

import com.wxy.beans.CacheKeyPrefix;

public class CacheKeyUtil {
    // 处理redis缓存key和CacheKeyPrefix

    public static String getCacheKey(String key, CacheKeyPrefix prefix){
        return prefix + "_" + key;
    }

}
