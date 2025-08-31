// ObjectRedisClient.java
package org.raghav.redis;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectRedisClient<T> extends AbstractRedisClient<T> {
    private final Class<T> clazz;
    private final ObjectMapper objectMapper;

    public ObjectRedisClient(RedisConfig config, Class<T> clazz) {
        super(config);
        this.clazz = clazz;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected String serializeValue(T value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    protected T deserializeValue(String value) {
        try { return objectMapper.readValue(value, clazz); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
