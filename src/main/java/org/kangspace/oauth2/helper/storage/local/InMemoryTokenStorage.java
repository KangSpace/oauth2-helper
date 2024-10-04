package org.kangspace.oauth2.helper.storage.local;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.engine.ElementAttributes;
import org.apache.commons.jcs.engine.behavior.IElementAttributes;
import org.kangspace.oauth2.helper.storage.TokenStorage;
import org.kangspace.oauth2.helper.token.Token;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * 本地缓存Token存储
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Slf4j
public class InMemoryTokenStorage<T extends Token> implements TokenStorage<T> {
    /**
     * 缓存名称
     */
    private static final String OAUTH2_TOKEN_CACHE = "oauth2TokenCache";
    /**
     * 获取jcs内存缓存
     */
    private final static CacheAccess<String, Token> TOKEN_CACHE = JCS.getInstance(OAUTH2_TOKEN_CACHE);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                TOKEN_CACHE.dispose();
                log.info("InMemoryTokenStorage shutdown finished");
            } catch (Exception e) {
                log.error("InMemoryTokenStorage shutdown error: {}", e.getMessage(), e);
            }
        }));
    }

    @Override
    public void set(@Nonnull String identity, @Nonnull T value) {
        String key = getKey(identity);
        IElementAttributes elementAttributes = new ElementAttributes();
        elementAttributes.setMaxLife(Objects.requireNonNull(value.getExpiresIn()));
        elementAttributes.setIsEternal(false);
        elementAttributes.setIsRemote(false);
        elementAttributes.setIsSpool(false);
        TOKEN_CACHE.put(key, value, elementAttributes);
        log.debug("InMemoryTokenStorage: set token to local cache, key: {}, value: {}", key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(@Nonnull String identity) {
        String key = getKey(identity);
        T token = (T) TOKEN_CACHE.get(key);
        log.debug("InMemoryTokenStorage: get token from local cache, key: {}, value: {}", key, token);
        return token;
    }
}
