package org.kangspace.oauth2.helper.request;

/**
 * 响应接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface Response {
    /**
     * 获取令牌是否获取成功
     *
     * @return 令牌是否获取成功
     */
    boolean isSucceed();

    /**
     * 获取令牌是否无效
     *
     * @return 令牌是否无效
     */
    boolean isInvalidToken();
}
