package com.han56.weather.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        
        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
        // 这个序列化器更灵活，能够处理各种数据类型
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用GenericJackson2JsonRedisSerializer
        template.setValueSerializer(jsonRedisSerializer);
        // hash的value序列化方式采用GenericJackson2JsonRedisSerializer
        template.setHashValueSerializer(jsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }
} 