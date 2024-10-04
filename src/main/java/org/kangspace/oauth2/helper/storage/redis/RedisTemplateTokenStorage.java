package org.kangspace.oauth2.helper.storage.redis;

import lombok.extern.slf4j.Slf4j;
import org.kangspace.devhelper.json.JsonParser;
import org.kangspace.oauth2.helper.storage.TokenStorage;
import org.kangspace.oauth2.helper.token.Token;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * 使用RedisTemplate实现TokenStorage <br>
 * <p>
 * 1. 可重写{@link #getKeyPrefix()}方法，自定义key前缀
 *
 * @author kango2gler@gmail.com
 * @since 2024/6/12
 */
@Slf4j
public abstract class RedisTemplateTokenStorage<T extends Token> implements TokenStorage<T> {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisTemplateTokenStorage(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取Token的Class
     *
     * @return Token的Class
     */
    protected abstract Class<T> getTokenClass();

    @Override
    public void set(@Nonnull String identity, @Nonnull T value) {
        String key = getKey(identity);
        redisTemplate.opsForValue().set(key, JsonParser.toJsonString(value), value.getExpiresIn(), TimeUnit.SECONDS);
        log.debug("RedisTemplateTokenStorage: set token to redis, key: {}, value: {}", key, value);
    }

    @Override
    public T get(@Nonnull String identity) {
        String key = getKey(identity);
        String token = redisTemplate.opsForValue().get(key);
        log.debug("RedisTemplateTokenStorage: get token from redis, key: {}, value: {}", key, token);
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return JsonParser.parse(token, getTokenClass());
    }
}
