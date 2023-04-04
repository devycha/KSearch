package com.ksearch.back.config.redis;

public class CacheKey {
    private CacheKey() {
    }
    public static final int DEFAULT_EXPIRE_SEC = 3600;
    public static final String USER = "user";
    public static final int USER_EXPIRE_SEC = 36000;
}
