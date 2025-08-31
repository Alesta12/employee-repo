package org.raghav.redis;
import redis.clients.jedis.Jedis;

public abstract class AbstractRedisClient<T> {

    protected final Jedis jedis;

    public AbstractRedisClient(RedisConfig config) {
        jedis = new Jedis(config.getHost(), config.getPort());
        if (config.getPassword() != null && !config.getPassword().isEmpty()) {
            jedis.auth(config.getPassword());
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
