package custom.keycloak.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Configuration
public class Redisconfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

//    @Bean
//    @Primary
//    public RedisCacheManager redisCacheManager(JedisConnectionFactory jedisConnectionFactory) {
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
//                .entryTtl(Duration.ofMinutes(1))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
//
//        redisCacheConfiguration.usePrefix();
//
//        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory)
//                .cacheDefaults(redisCacheConfiguration).build();
//
//    }

    @Bean
    RedisTemplate<String, List<String>> redisTemplate() {
        RedisTemplate<String, List<String>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new JsonRedisSerializer());
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
