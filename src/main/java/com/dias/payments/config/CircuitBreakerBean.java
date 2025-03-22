package com.dias.payments.config;

import com.dias.payments.circuitBreaker.CircuitBreakerCustom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerBean {
    @Bean
    public CircuitBreakerCustom circuitBreakerCustom () { return new CircuitBreakerCustom();}
}
