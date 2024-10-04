package org.kangspace.oauth2.helper.request;

import org.apache.http.client.HttpClient;
import org.kangspace.oauth2.helper.OAuth2RequestService;
import org.kangspace.oauth2.helper.token.Token;

import java.util.Map;

/**
 * 默认Request请求实现
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public class DefaultRequest<Req, Resp extends Response, T extends Token> extends AbstractRequest<Req, Resp, T> {

    /**
     * 构造方法(用于Get/DELETE请求)
     *
     * @param url                  请求的URL
     * @param httpHeaders          请求的请求头
     * @param responseClass        响应类
     * @param needToken            是否需要Token
     * @param httpClient           HttpClient
     * @param oauth2RequestService OAuth2RequestService
     */
    public DefaultRequest(String url, HttpMethod httpMethod, Map<String, String> httpHeaders, Class<Resp> responseClass, boolean needToken,
                          HttpClient httpClient, OAuth2RequestService<T> oauth2RequestService) {
        super(url, httpMethod, httpHeaders, responseClass, needToken, httpClient, oauth2RequestService);
    }

    /**
     * 构造方法(用于Post/Put请求)
     *
     * @param url           请求的URL
     * @param httpHeaders   请求的请求头
     * @param requestBody   请求对象
     * @param responseClass 响应类
     * @param needToken     是否需要Token
     * @param httpClient    HttpClient
     */
    public DefaultRequest(String url, HttpMethod httpMethod, Map<String, String> httpHeaders, Req requestBody,
                          Class<Resp> responseClass, boolean needToken, HttpClient httpClient,
                          OAuth2RequestService<T> oauth2RequestService) {
        super(url, httpMethod, httpHeaders, requestBody, responseClass, needToken, httpClient, oauth2RequestService);
    }
}
