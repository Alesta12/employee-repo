package org.raghav.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
public class IpRateLimitingFilter implements ContainerRequestFilter {

    // Store buckets per IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String ip = getClientIp(requestContext);

        Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);

        if (!bucket.tryConsume(1)) {
            // Too many requests
            requestContext.abortWith(Response.status(429) // 429 = Too Many Requests
                    .entity("Rate limit exceeded. Try again later.")
                    .build());
        }
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(
                10, // 10 requests
                Refill.greedy(10, Duration.ofMinutes(1)) // per 1 minute
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(ContainerRequestContext requestContext) {
        // First check headers (in case behind proxy/load balancer)
        String forwarded = requestContext.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim(); // first IP in the chain
        }
        return requestContext.getUriInfo().getRequestUri().getHost(); // fallback
    }
}
