package org.raghav.redis;

import io.github.cdimascio.dotenv.Dotenv;

public class RedisConfig {

    private final String redisUrl;
    public RedisConfig() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        this.redisUrl = dotenv.get("REDIS_URI");
    }

    public RedisConfig(String redisUrl) {
        this.redisUrl = redisUrl;
    }

    public String getRedisUrl() {
        return redisUrl;
    }
}
