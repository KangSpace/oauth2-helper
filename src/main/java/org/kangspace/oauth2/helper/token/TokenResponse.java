package org.kangspace.oauth2.helper.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.kangspace.oauth2.helper.request.Response;

/**
 * Token响应接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface TokenResponse<T extends Token> extends Token, Response {

    /**
     * 获取Token对象
     *
     * @return Token对象
     */
    @SuppressWarnings("unchecked")
    @JsonIgnore
    default T getToken() {
        return (T) this;
    }
}
