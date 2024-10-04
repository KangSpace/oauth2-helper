package org.kangspace.oauth2.helper.storage;

import javax.annotation.Nonnull;

/**
 * 存储接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface Storage<T> {
    /**
     * 设置缓存
     *
     * @param identity   唯一标识
     * @param value      value
     * @param ttlSeconds 超时时间(秒)
     */
    void set(@Nonnull String identity, @Nonnull T value, @Nonnull Long ttlSeconds);

    /**
     * 获取缓存
     *
     * @param identity 唯一标识
     * @return {@link T}
     */
    T get(@Nonnull String identity);

    /**
     * 获取key前缀(自定义key格式)
     *
     * @return key前缀
     */
    String getKeyPrefix();

    /**
     * 获取key
     *
     * @param identity 唯一标识
     * @return key
     */
    default String getKey(@Nonnull String identity) {
        return getKeyPrefix() + identity;
    }
}
