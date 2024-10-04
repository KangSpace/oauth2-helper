package org.kangspace.oauth2.helper.request;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.kangspace.oauth2.helper.OAuth2RequestService;
import org.kangspace.oauth2.helper.token.Token;

import java.util.Map;

/**
 * 请求接口
 *
 * @param <Req>  请求体类型
 * @param <Resp> 响应体类型
 */
public interface Request<Req, Resp extends Response, T extends Token> {
    /**
     * Bearer头前缀
     */
    String BEARER_HEADER_PREFIX = "Bearer ";

    /**
     * 请求的URL
     *
     * @return URL
     */
    String getUrl();

    /**
     * 请求的类型
     *
     * @return {@link HttpMethod}
     */
    HttpMethod getHttpMethod();

    /**
     * 请求体
     *
     * @return requestBody
     */
    Req getRequestBody();

    /**
     * 响应对象
     *
     * @return 响应对象
     */
    Class<Resp> getResponseClass();

    /**
     * 请求的请求头
     *
     * @return {@link HttpHeaders}
     */
    Map<String, String> getHttpHeaders();

    /**
     * 是否需要AccessToken
     *
     * @return boolean
     */
    boolean isNeedToken();

    /**
     * 获取请求客户端
     *
     * @return {@link HttpClient}
     */
    HttpClient getHttpClient();

    /**
     * 获取OAuth2Service,用于获取和刷新Token
     *
     * @return {@link OAuth2RequestService}
     */
    OAuth2RequestService<T> getOAuth2RequestService();

    /**
     * 执行请求
     *
     * @return Resp
     */
    Resp execute();

    /**
     * 执行请求具体操作
     *
     * @param refreshToken 是否刷新Token
     * @return Resp
     */
    Resp doExecute(boolean refreshToken);
}
