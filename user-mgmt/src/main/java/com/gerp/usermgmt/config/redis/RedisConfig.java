package com.gerp.usermgmt.config.redis;

import com.gerp.shared.configuration.CacheKeyGenerator;
import com.gerp.usermgmt.pojo.MobilePrivilegeCacheDto;
import com.gerp.usermgmt.pojo.PrivilegeCacheDto;
import com.gerp.usermgmt.pojo.ScreenGroupPrivilegeCacheDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

        @Bean
        public RedisCacheConfiguration cacheConfiguration() {
            return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(36000)).disableCachingNullValues().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        }

    @Primary
    @Bean(name = "cacheManager")
    public CacheManager cacheManager() {
        return new RedisCacheManager(RedisCacheWriter.lockingRedisCacheWriter(jedisConnectionFactory()), cacheConfiguration());
    }

    @Bean("customKeyGenerator")
    public CacheKeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }


    @Bean
    RedisTemplate<String, List<String>> redisTemplate() {
        RedisTemplate<String, List<String>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, Long> redisTemplateId() {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, PrivilegeCacheDto> redisTemplateForPrivilegeCache() {
        RedisTemplate<String, PrivilegeCacheDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, MobilePrivilegeCacheDto> redisTemplateForMobilePrivilegeCache() {
        RedisTemplate<String, MobilePrivilegeCacheDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, ScreenGroupPrivilegeCacheDto> redisTemplateForScreenGroupPrivilegeCache() {
        RedisTemplate<String, ScreenGroupPrivilegeCacheDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

//    static class JsonRedisSerializer implements RedisSerializer<Object> {
//
//        private final ObjectMapper om;
//
//        public JsonRedisSerializer() {
//            this.om = new ObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        }
//
//        @Override
//        public byte[] serialize(Object t) throws SerializationException {
//            try {
//                return om.writeValueAsBytes(t);
//            } catch (JsonProcessingException e) {
//                throw new SerializationException(e.getMessage(), e);
//            }
//        }
//
//        @Override
//        public Object deserialize(byte[] bytes) throws SerializationException {
//
//            if(bytes == null){
//                return null;
//            }
//
//            try {
//                return om.readValue(bytes, Object.class);
//            } catch (Exception e) {
//                throw new SerializationException(e.getMessage(), e);
//            }
//        }
//    }
}
