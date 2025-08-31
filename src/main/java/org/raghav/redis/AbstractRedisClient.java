package org.raghav.redis;

import redis.clients.jedis.Jedis;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractRedisClient<T> {

    protected final Jedis jedis;

    public AbstractRedisClient(RedisConfig config) {
        try {
            // Parse the full Redis URL (rediss://username:password@host:port)
            URI redisUri = new URI(config.getRedisUrl());
            jedis = new Jedis(redisUri,10000,10000);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid Redis URL: " + config.getRedisUrl(), e);
        }
    }

    protected String serializeKey(String key) {
        return key;
    }

    protected abstract String serializeValue(T value);

    protected abstract T deserializeValue(String value);

    public void set(String key, T value, int ttlSeconds) {
        jedis.setex(serializeKey(key), ttlSeconds, serializeValue(value));
    }

    public T get(String key) {
        String value = jedis.get(serializeKey(key));
        return value == null ? null : deserializeValue(value);
    }

    public void delete(String key) {
        jedis.del(serializeKey(key));
    }

    public boolean exists(String key) {
        return jedis.exists(serializeKey(key));
    }
}
