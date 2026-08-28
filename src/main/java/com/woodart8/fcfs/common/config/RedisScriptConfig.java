package com.woodart8.fcfs.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<Long> couponReserveScript() {

        DefaultRedisScript<Long> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        "redis/coupon-reserve.lua"
                )
        );

        script.setResultType(Long.class);

        return script;
    }

    @Bean
    public RedisScript<String> moveToProcessingScript() {

        DefaultRedisScript<String> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        "redis/move-to-processing.lua"
                )
        );

        script.setResultType(String.class);

        return script;
    }
}
