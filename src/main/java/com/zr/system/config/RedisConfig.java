package com.zr.system.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
//    @Bean
//    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
//        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
//        RedisCacheConfiguration config = RedisCacheConfiguration
//                .defaultCacheConfig();
//
//        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair
//                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
//        if (redisProperties.getTimeToLive() != null) {
//            config = config.entryTtl(redisProperties.getTimeToLive());
//        }
//        if (redisProperties.getKeyPrefix() != null) {
//            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
//        }
//        if (!redisProperties.isCacheNullValues()) {
//            config = config.disableCachingNullValues();
//        }
//        if (!redisProperties.isUseKeyPrefix()) {
//            config = config.disableKeyPrefix();
//        }
//        return config;
//
//    }

}
