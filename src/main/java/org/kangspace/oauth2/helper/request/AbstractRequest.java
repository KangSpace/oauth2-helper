package org.kangspace.oauth2.helper.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.kangspace.devhelper.CollectionUtil;
import org.kangspace.devhelper.json.JsonParser;
import org.kangspace.oauth2.helper.OAuth2RequestService;
import org.kangspace.oauth2.helper.exception.UnOKResponseException;
import org.kangspace.oauth2.helper.exception.UnSucceedResponseException;
import org.kangspace.oauth2.helper.exception.UnauthorizedException;
import org.kangspace.oauth2.helper.token.Token;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象Request请求实现
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Data
@Slf4j
public abstract class AbstractRequest<Req, Resp extends Response, T extends Token> implements Request<Req, Resp, T> {
    /**
     * 请求的URL
     */
    private String url;

    /**
     * 请求的类型
     */
    private HttpMethod httpMethod;

    /**
     * 请求的请求头
     */
    private Map<String, String> httpHeaders;

    /**
     * 请求对象
     */
    private Req requestBody;

    /**
     * 响应类
     */
    private Class<Resp> responseClass;

    /**
     * 请求Client
     */
    private HttpClient httpClient;

    /**
     * 是否需要Token
     */
    private boolean needToken;

    /**
     * 获取Token相关Service
     */
    private OAuth2RequestService<T> oAuth2RequestService;

    /**
     * 构造方法(用于Get/DELETE请求)
     *
     * @param url                  请求的URL
     * @param httpMethod           请求的类型
     * @param httpHeaders          请求的请求头
     * @param responseClass        响应类
     * @param needToken            是否需要Token
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     */
    public AbstractRequest(String url, HttpMethod httpMethod, Map<String, String> httpHeaders, Class<Resp> responseClass,
                           boolean needToken, HttpClient httpClient, OAuth2RequestService<T> oAuth2RequestService) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.httpHeaders = httpHeaders;
        this.responseClass = responseClass;
        this.needToken = needToken;
        this.httpClient = httpClient;
        this.oAuth2RequestService = oAuth2RequestService;
    }

    /**
     * 构造方法(用于Post/Put请求)
     *
     * @param url                  请求的URL
     * @param httpMethod           请求的类型
     * @param httpHeaders          请求的请求头
     * @param requestBody          请求对象
     * @param responseClass        响应类
     * @param needToken            是否需要Token
     * @param httpClient           HttpClient
     * @param oAuth2RequestService OAuth2RequestService
     */
    public AbstractRequest(String url, HttpMethod httpMethod, Map<String, String> httpHeaders, Req requestBody,
                           Class<Resp> responseClass, boolean needToken, HttpClient httpClient,
                           OAuth2RequestService<T> oAuth2RequestService) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
        this.responseClass = responseClass;
        this.needToken = needToken;
        this.httpClient = httpClient;
        this.oAuth2RequestService = oAuth2RequestService;
    }

    /**
     * 设置请求时的Token <br>
     * 默认实现为: 请求头添加Authorization BearerToken <br>
     * 子类可重写该方法实现自定义的Token设置方式, 如在URL中添加token参数<br>
     *
     * @param request 请求
     * @param token   令牌
     */
    public void tokenSet4Request(HttpRequestBase request, String token) {
        boolean isSet = getOAuth2RequestService().tokenSet4Request(request, token);
        if (!isSet) {
            request.removeHeaders(HttpHeaders.AUTHORIZATION);
            request.setHeader(HttpHeaders.AUTHORIZATION,
                    BEARER_HEADER_PREFIX + token);
        }
    }

    @Override
    public Resp execute() {
        log.debug("request execute: request: {}", this);
        boolean refreshToken = false;
        Resp response = null;
        try {
            response = doExecute(false);
        } catch (UnauthorizedException e) {
            refreshToken = true;
        }
        // Token过期, 刷新token重新请求
        if (refreshToken || response.isInvalidToken()) {
            log.info("request execute: token invalid, refresh token and retry");
            response = doExecute(true);
        }
        if (response != null && !response.isSucceed()) {
            throw new UnSucceedResponseException(JsonParser.toJsonString(response));
        }
        return response;
    }

    @Override
    public Resp doExecute(boolean isRefreshToken) {
        String uri = getUrl();
        HttpMethod method = getHttpMethod();
        Map<String, String> headers = getHttpHeaders();
        log.debug("request doExecute: url: {}, method: {}, httpHeader: {}, requestBody: {}, responseClass: {}", uri,
                method, headers, getRequestBody(), getResponseClass());

        HttpRequestBase request;

        if (HttpMethod.GET.equals(method)) {
            request = new HttpGet(uri);
        } else if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method)) {
            if (HttpMethod.POST.equals(method)) {
                request = new HttpPost(uri);
            } else {
                request = new HttpPut(uri);
            }
            if (Objects.nonNull(requestBody)) {
                // 请求体赋值
                ((HttpEntityEnclosingRequestBase) request)
                        .setEntity(new StringEntity(JsonParser.toJsonString(requestBody), StandardCharsets.UTF_8));
            }
        } else {
            throw new UnsupportedOperationException("不支持的请求方法");
        }
        // 请求头赋值
        if (CollectionUtil.isNotEmpty(headers)) {
            headers.forEach(request::addHeader);
        }
        // 请求头Token处理
        if (needToken) {
            tokenSet4Request(request, getOAuth2RequestService().getToken(isRefreshToken).getAccessToken());
        }
        try {
            log.debug("request doExecute: send request begin, url: {}, content: {}", uri,
                    JsonParser.toJsonString(requestBody));
            HttpResponse response = getHttpClient().execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            InputStream bodyStream = response.getEntity().getContent();
            String responseBody = IOUtils.toString(bodyStream, StandardCharsets.UTF_8);
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new UnauthorizedException("请求异常: 状态码: " + statusCode + ", 错误信息: " + responseBody);
            }
            if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
                throw new UnOKResponseException(statusCode, request, responseBody);
            }
            log.debug("request doExecute: send request success, uri: {}, responseBody: {}", uri, responseBody);
            return JsonParser.parse(responseBody, responseClass);
        } catch (IOException e) {
            throw new RuntimeException("请求异常, error:" + e.getMessage(), e);
        }
    }
}
