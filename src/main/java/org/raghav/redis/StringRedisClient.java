package org.raghav.redis;

public class StringRedisClient extends AbstractRedisClient<String> {

    public StringRedisClient(RedisConfig config) {
        super(config);
    }

    @Override
    protected String serializeValue(String value) {
        return value;
    }

    @Override
    protected String deserializeValue(String value) {
        return value;
    }
}
