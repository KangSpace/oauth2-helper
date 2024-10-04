package org.kangspace.oauth2.helper;

import lombok.NonNull;
import org.kangspace.oauth2.helper.token.Token;
import org.kangspace.oauth2.helper.token.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 客户端模式OAuth2请求服务Service接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface ClientCredentialsService<T extends Token> extends OAuth2RequestService<T> {
    Logger log = LoggerFactory.getLogger(ClientCredentialsService.class);

    /**
     * 获取token <br>
     * grant_type: client_credentials
     *
     * @param clientId     客户端ID
     * @param clientSecret 客户端密钥
     * @return Token
     */
    TokenResponse<T> getToken(@NonNull String clientId, @NonNull String clientSecret);


    /**
     * 获取token <br>
     * grant_type: client_credentials
     *
     * @return Token
     */
    default T getToken(boolean isRefreshToken) {
        String clientId = getClientId();
        log.debug("getToken, refreshToken: {}, clientId: {}", isRefreshToken, clientId);
        T token = getTokenStorage().get(clientId);
        if (isRefreshToken || token == null) {
            log.debug("cache token not exist or need refresh token, clientId: {}, isRefreshToken: {}", clientId, isRefreshToken);
            TokenResponse<T> tokenResponse = null;

            if (useRefreshToken() && token != null) {
                log.debug("specific useRefreshToken, clientId: {}, refreshToken: {}", clientId, token.getRefreshToken());
                try {
                    tokenResponse = refreshToken(token.getRefreshToken(), Objects.requireNonNull(getClientId()), Objects.requireNonNull(getClientSecret()));
                } catch (Exception e) {
                    log.error("specific useRefreshToken exception, error: {}", e.getMessage(), e);
                }
            }

            if (tokenResponse == null) {
                log.debug("get token from server by clientId/clientSecret, clientId: {}", clientId);
                tokenResponse = getToken(Objects.requireNonNull(getClientId()), Objects.requireNonNull(getClientSecret()));
            }

            if (tokenResponse != null) {
                log.debug("get token from server success, cache token, clientId: {}", clientId);
                token = tokenResponse.getToken();
                getTokenStorage().set(getClientId(), token);
            }
        }
        return token;
    }
}
