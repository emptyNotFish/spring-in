package cn.ocoop.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Optional;

@Configuration
@DependsOn(value = "appContextRedis")
public class SpringDataRedisConfig {
    private static final Logger log = LoggerFactory.getLogger(SpringDataRedisConfig.class);
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisConfig redisConfig = getRedisConfig();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName(redisConfig.getHost());
        jedisConnectionFactory.setPort(redisConfig.getPort());
        Optional.ofNullable(redisConfig.getPassword()).ifPresent(jedisConnectionFactory::setPassword);
        Optional.ofNullable(redisConfig.getTimeout()).ifPresent(jedisConnectionFactory::setTimeout);
        return jedisConnectionFactory;
    }

    private RedisConfig getRedisConfig() {
        RedisConfig redisConfig = null;
        try {
            redisConfig = AppContextRedis.getBean(RedisConfigService.class).getRedisConfig();
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("RedisConfigService未配置");
        }
        if (redisConfig == null) redisConfig = new RedisConfig("127.0.0.1", 6379, null, null);
        return redisConfig;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }


}
