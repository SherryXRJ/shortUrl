package pers.sherry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Bean
    @ConfigurationProperties(prefix = "redis.jedis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        return new JedisPoolConfig();
    }

    @Bean
    public JedisShardInfo jedisShardInfo() {
        return new JedisShardInfo(host, port);
    }

    @Bean
    public ShardedJedisPool shardedJedisPool(JedisPoolConfig jedisPoolConfig, JedisShardInfo jedisShardInfo) {
        List<JedisShardInfo> list = new ArrayList<>();
        list.add(jedisShardInfo);
        return new ShardedJedisPool(jedisPoolConfig, list);
    }

    @Bean
    public ShardedJedis shardedJedis(ShardedJedisPool shardedJedisPool){
        return shardedJedisPool.getResource();
    }
}
