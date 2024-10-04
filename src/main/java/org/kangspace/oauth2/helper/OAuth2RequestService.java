package org.kangspace.oauth2.helper;

import lombok.NonNull;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.kangspace.oauth2.helper.request.RequestFactory;
import org.kangspace.oauth2.helper.request.Response;
import org.kangspace.oauth2.helper.storage.TokenStorage;
import org.kangspace.oauth2.helper.storage.local.InMemoryTokenStorage;
import org.kangspace.oauth2.helper.storage.redis.RedisTemplateTokenStorage;
import org.kangspace.oauth2.helper.storage.redis.RedissonTokenStorage;
import org.kangspace.oauth2.helper.token.Token;
import org.kangspace.oauth2.helper.token.TokenResponse;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * OAuth2请求服务接口
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public interface OAuth2RequestService<T extends Token> {

    /**
     * 默认HttpClient
     */
    AtomicReference<HttpClient> DEFAULT_HTTP_CLIENT = new AtomicReference<>(null);

    /**
     * 获取客户端ID
     *
     * @return 客户端ID
     */
    String getClientId();

    /**
     * 获取客户端密钥
     *
     * @return 客户端密钥
     */
    String getClientSecret();

    /**
     * 获取HttpClient
     * <p>
     * 实现类可重写,建议自定义HttpClient
     * <p>
     * 默认使用{@link RequestFactory#getHttpClient()}
     *
     * @return HttpClient
     */
    default HttpClient getHttpClient() {
        synchronized (DEFAULT_HTTP_CLIENT) {
            if (DEFAULT_HTTP_CLIENT.get() == null) {
                DEFAULT_HTTP_CLIENT.set(RequestFactory.getHttpClient());
            }
        }
        return DEFAULT_HTTP_CLIENT.get();
    }

    /**
     * 获取token存储对象
     *
     * @return token存储对象
     * @see TokenStorage
     * @see RedisTemplateTokenStorage
     * @see RedissonTokenStorage
     * @see InMemoryTokenStorage
     */
    default TokenStorage<T> getTokenStorage() {
        return new InMemoryTokenStorage<T>();
    }

    /**
     * 是否使用刷新token
     *
     * @return 是否使用刷新token, 默认不使用
     */
    default boolean useRefreshToken() {
        return false;
    }

    /**
     * 获取token
     *
     * @param isRefreshToken 是否使用刷新token
     * @return token
     */
    T getToken(boolean isRefreshToken);

    /**
     * 刷新token <br>
     * grant_type: client_credentials
     *
     * @param clientId     客户端ID
     * @param clientSecret 客户端密钥
     * @return Token
     */
    default TokenResponse<T> refreshToken(@NonNull String refreshToken, @NonNull String clientId, @NonNull String clientSecret) {
        throw new UnsupportedOperationException("refreshToken is not supported");
    }

    /**
     * 创建GET请求
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return GET请求对象
     */
    default <Resp extends Response> Resp get(@NonNull String url, Map<String, String> httpHeaders,
                                             @NonNull Class<Resp> responseClass) {
        return RequestFactory.get(url, httpHeaders, responseClass, getHttpClient(), this).execute();
    }

    /**
     * 创建GET请求(有Token)
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return GET请求对象
     */
    default <Resp extends Response> Resp getWithToken(@NonNull String url, Map<String, String> httpHeaders,
                                                      @NonNull Class<Resp> responseClass) {
        return RequestFactory.getWithToken(url, httpHeaders, responseClass, getHttpClient(), this).execute();
    }

    /**
     * /**
     * 创建DELETE请求
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return DELETE请求对象
     */
    default <Resp extends Response> Resp delete(@NonNull String url, Map<String, String> httpHeaders,
                                                @NonNull Class<Resp> responseClass) {
        return RequestFactory.delete(url, httpHeaders, responseClass, getHttpClient(), this).execute();
    }

    /**
     * 创建DELETE请求(有Token)
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return DELETE请求对象
     */
    default <Resp extends Response> Resp deleteWithToken(@NonNull String url, Map<String, String> httpHeaders,
                                                         @NonNull Class<Resp> responseClass) {
        return RequestFactory.deleteWithToken(url, httpHeaders, responseClass, getHttpClient(), this).execute();
    }

    /**
     * 创建POST请求
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return POST请求对象
     */
    default <Req, Resp extends Response> Resp post(@NonNull String url, Map<String, String> httpHeaders,
                                                   Req requestBody, @NonNull Class<Resp> responseClass) {
        return RequestFactory.post(url, httpHeaders, requestBody, responseClass, getHttpClient(), this).execute();
    }

    /**
     * 创建POST请求(有Token)
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return POST请求对象
     */
    default <Req, Resp extends Response> Resp postWithToken(@NonNull String url, Map<String, String> httpHeaders,
                                                            Req requestBody, @NonNull Class<Resp> responseClass) {
        return RequestFactory.postWithToken(url, httpHeaders, requestBody, responseClass, getHttpClient(), this)
                .execute();
    }

    /**
     * 创建PUT请求
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return PUT请求对象
     */
    default <Req, Resp extends Response> Resp put(@NonNull String url, Map<String, String> httpHeaders,
                                                  Req requestBody, @NonNull Class<Resp> responseClass) {
        return RequestFactory.put(url, httpHeaders, requestBody, responseClass, getHttpClient(), this).execute();
    }

    /**
     * 创建PUT请求(有Token)
     *
     * @param url           请求URL
     * @param httpHeaders   请求头
     * @param responseClass 响应类
     * @param httpClient    HttpClient
     * @return PUT请求对象
     */
    default <Req, Resp extends Response> Resp putWithToken(@NonNull String url, Map<String, String> httpHeaders,
                                                           Req requestBody, @NonNull Class<Resp> responseClass) {
        return RequestFactory.putWithToken(url, httpHeaders, requestBody, responseClass, getHttpClient(), this).execute();
    }

    /**
     * 设置请求时的Token
     *
     * @param request 请求
     * @param token   令牌
     * @return 是否已设置
     */
    default boolean tokenSet4Request(HttpRequestBase request, String token) {
        return false;
    }
}
