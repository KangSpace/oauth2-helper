package org.kangspace.oauth2.helper;

import lombok.NonNull;
import org.kangspace.oauth2.helper.token.Token;
import org.kangspace.oauth2.helper.token.TokenResponse;

import java.util.Objects;

/**
 * 密码模式 OAuth2请求服务Service接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface PasswordService<T extends Token> extends OAuth2RequestService<T> {

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 获取token <br>
     * grant_type: password <br>
     *
     * @param clientId     客户端ID
     * @param clientSecret 客户端密钥
     * @param username     用户名
     * @param password     密码
     * @return Token
     */
    TokenResponse<T> getToken(@NonNull String clientId, @NonNull String clientSecret,
                              @NonNull String username, @NonNull String password);

    /**
     * 获取token <br>
     * grant_type: password <br>
     *
     * @return Token
     */
    default T getToken(boolean isRefreshToken) {
        T token = getTokenStorage().get(getClientId());
        if (isRefreshToken || token == null) {
            TokenResponse<T> tokenResponse = getToken(Objects.requireNonNull(getClientId()),
                    Objects.requireNonNull(getClientSecret()),
                    Objects.requireNonNull(getUsername()), Objects.requireNonNull(getPassword()));
            if (tokenResponse != null) {
                token = tokenResponse.getToken();
                getTokenStorage().set(getClientId(), token);
            }
        }
        return token;
    }
}
