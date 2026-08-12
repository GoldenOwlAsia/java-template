package com.goldenowl.springboottemplate.app.config;

import com.goldenowl.springboottemplate.app.constant.CacheConstant;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheConfig {

  @Bean
  RedisCacheConfiguration redisCacheConfiguration() {
    return baseCacheConfiguration().entryTtl(CacheConstant.DEFAULT_TTL);
  }

  @Bean
  RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
      RedisCacheConfiguration redisCacheConfiguration) {
    return builder ->
        builder.withInitialCacheConfigurations(
            Map.of(
                CacheConstant.ARTICLE_DETAIL,
                redisCacheConfiguration.entryTtl(CacheConstant.ARTICLE_DETAIL_TTL)));
  }

  private static RedisCacheConfiguration baseCacheConfiguration() {
    RedisSerializationContext.SerializationPair<Object> jsonValues =
        RedisSerializationContext.SerializationPair.fromSerializer(
            GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(
                    BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.goldenowl.")
                        .allowIfSubType("java.util.")
                        .build())
                .build());

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(jsonValues)
        .disableCachingNullValues();
  }
}
