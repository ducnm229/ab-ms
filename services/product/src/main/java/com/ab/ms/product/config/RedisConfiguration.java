package com.ab.ms.product.config;

import com.ab.ms.product.dto.CatalogPage;
import com.ab.ms.product.dto.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, CatalogPage> catalogRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        return redisTemplate(connectionFactory, objectMapper, CatalogPage.class);
    }

    @Bean
    public RedisTemplate<String, ProductResponse> productRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        return redisTemplate(connectionFactory, objectMapper, ProductResponse.class);
    }

    private static <T> RedisTemplate<String, T> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            Class<T> valueType) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, valueType);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
