package com.dias.payments.circuitBreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.time.Duration;
import java.util.function.Supplier;

public class CircuitBreakerCustom {
    private CircuitBreaker circuitBreaker;

    public void circuitBreakerService() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(10)
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .slidingWindow(10, 3, CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .recordExceptions(RuntimeException.class, Exception.class)
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.circuitBreaker = registry.circuitBreaker("DefaultCircuitBreaker");
    }

    public <T> T executeWithCircuitBreaker(Supplier<T> supplier, Supplier<T> fallback) {
        Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            System.out.println(e);
            return fallback.get();
        }
    }

}
