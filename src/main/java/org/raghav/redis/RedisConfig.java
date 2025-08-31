// RedisConfig.java
package org.raghav.redis;

public class RedisConfig {
    private final String host = "localhost";
    private final int port = 6379;
    private final String password = null;

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getPassword() { return password; }
}
