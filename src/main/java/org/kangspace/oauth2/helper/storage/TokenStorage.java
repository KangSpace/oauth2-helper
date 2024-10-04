package org.kangspace.oauth2.helper.storage;

import org.kangspace.oauth2.helper.token.Token;

import javax.annotation.Nonnull;

/**
 * Token存储接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface TokenStorage<T extends Token> extends Storage<T> {
    /**
     * 缓存key前缀
     */
    String DEFAULT_TOKEN_KEY_PREFIX = "oauth2-helper:token:";

    /**
     * 设置令牌
     *
     * @param identity 令牌唯一标识
     * @param value    令牌
     */
    void set(@Nonnull String identity, @Nonnull T value);

    /**
     * 设置令牌
     *
     * @param identity   令牌唯一标识
     * @param value      令牌
     * @param ttlSeconds 过期时间，单位秒
     */
    @Override
    default void set(@Nonnull String identity, @Nonnull T value, @Nonnull Long ttlSeconds) {
        set(identity, value);
    }

    @Override
    default String getKeyPrefix() {
        return DEFAULT_TOKEN_KEY_PREFIX;
    }
}
