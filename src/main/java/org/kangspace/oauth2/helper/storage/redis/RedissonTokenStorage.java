package org.kangspace.oauth2.helper.storage.redis;

import lombok.extern.slf4j.Slf4j;
import org.kangspace.oauth2.helper.storage.TokenStorage;
import org.kangspace.oauth2.helper.token.Token;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * 使用Redisson实现TokenStorage <br>
 * <p>
 * 1. 可重写{@link #getKeyPrefix()}方法，自定义key前缀
 *
 * @author kango2gler@gmail.com
 * @since 2024/6/12
 */
@Slf4j
public class RedissonTokenStorage<T extends Token> implements TokenStorage<T> {
    private final RedissonClient redissonClient;

    public RedissonTokenStorage(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void set(@Nonnull String identity, @Nonnull T value) {
        String key = getKey(identity);
        redissonClient.getBucket(key, JsonJacksonCodec.INSTANCE).set(value, Duration.ofSeconds(value.getExpiresIn()));
        log.debug("RedissonTokenStorage: set token to redis, key: {}, value: {}", key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(@Nonnull String identity) {
        String key = getKey(identity);
        T token = (T) redissonClient.getBucket(key, JsonJacksonCodec.INSTANCE).get();
        log.debug("RedissonTokenStorage: get token from redis, key: {}, value: {}", key, token);
        return token;
    }
}
