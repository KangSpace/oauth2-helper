package org.kangspace.oauth2.helper.token;

/**
 * Token实体接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface Token {

    /**
     * 获取访问令牌
     *
     * @return 访问令牌字符串
     */
    String getAccessToken();

    /**
     * 获取令牌过期时间
     *
     * @return 过期时间（秒）
     */
    Long getExpiresIn();

    /**
     * 获取刷新令牌（如果有）
     *
     * @return 刷新令牌字符串，如果不支持刷新则返回null
     */
    default String getRefreshToken() {
        return null;
    }

    /**
     * 获取令牌类型
     *
     * @return 令牌类型字符串（例如："Bearer"）
     */
    default String getTokenType() {
        return null;
    }
}
