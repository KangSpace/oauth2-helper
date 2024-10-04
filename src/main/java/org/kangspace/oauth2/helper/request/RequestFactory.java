package org.kangspace.oauth2.helper.request;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kangspace.oauth2.helper.OAuth2RequestService;
import org.kangspace.oauth2.helper.token.Token;

import java.util.Map;

/**
 * Request工厂类
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public class RequestFactory {
    /**
     * 默认最大重试次数
     */
    private static final int DEFAULT_MAX_RETRIES = 3;

    /**
     * 获取HttpClient对象 <br>
     * <pre>
     * 基础配置:
     * 超时时间: 60s
     * 连接超时时间: 30s
     * 读取超时时间: 60s
     * 最大连接数: 200
     * 每个路由的最大连接数: 20
     * 重试机制: IOException时 重试3次(不含首次请求)
     * </pre>
     *
     * @return HttpClient
     */
    public static CloseableHttpClient getHttpClient() {
        // 创建自定义的RequestConfig
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(30000)
                .setSocketTimeout(60000)
                .build();
        // 添加重试机制
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(20);

        // 设置重试处理器
        builder.setRetryHandler((exception, executionCount, context) -> {
            if (executionCount >= DEFAULT_MAX_RETRIES) {
                return false;
            }
            // 如果是IOException，进行重试
            return exception instanceof java.io.IOException;
        });
        return builder.build();
    }

    /**
     * 创建Get请求(无Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return Get请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> get(
            String url,
            Map<String, String> httpHeaders,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.GET, httpHeaders, responseClass, false, httpClient, oAuth2RequestService);
    }

    /**
     * 创建Get请求(有Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return Get请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> getWithToken(
            String url,
            Map<String, String> httpHeaders,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.GET, httpHeaders, responseClass, true, httpClient, oAuth2RequestService);
    }

    /**
     * 创建DELETE请求(无Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return DELETE请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> delete(
            String url,
            Map<String, String> httpHeaders,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.DELETE, httpHeaders, responseClass, false, httpClient, oAuth2RequestService);
    }

    /**
     * 创建DELETE请求(有Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return DELETE请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> deleteWithToken(
            String url,
            Map<String, String> httpHeaders,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.DELETE, httpHeaders, responseClass, true, httpClient, oAuth2RequestService);
    }


    /**
     * 创建Post请求(无Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param requestBody          请求体
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return Post请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> post(
            String url,
            Map<String, String> httpHeaders,
            Req requestBody,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.POST, httpHeaders, requestBody, responseClass, false, httpClient, oAuth2RequestService);
    }

    /**
     * 创建Post请求(有Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return Post请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> postWithToken(
            String url,
            Map<String, String> httpHeaders,
            Req requestBody,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.POST, httpHeaders, requestBody, responseClass, true, httpClient, oAuth2RequestService);
    }

    /**
     * 创建Put请求(无Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return Put请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> put(
            String url,
            Map<String, String> httpHeaders,
            Req requestBody,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.PUT, httpHeaders, requestBody, responseClass, false, httpClient, oAuth2RequestService);
    }

    /**
     * 创建Put请求(有Token)
     *
     * @param url                  请求URL
     * @param httpHeaders          请求头
     * @param responseClass        响应类
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     * @return Put请求对象
     */
    public static <Req, Resp extends Response, T extends Token> Request<Req, Resp, T> putWithToken(
            String url,
            Map<String, String> httpHeaders,
            Req requestBody,
            Class<Resp> responseClass,
            HttpClient httpClient,
            OAuth2RequestService<T> oAuth2RequestService) {
        return new DefaultRequest<Req, Resp, T>(url, HttpMethod.PUT, httpHeaders, requestBody, responseClass, true, httpClient, oAuth2RequestService);
    }

}
