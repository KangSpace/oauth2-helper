package org.kangspace.oauth2.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kangspace.devhelper.http.UrlHelper;
import org.kangspace.oauth2.helper.request.RequestFactory;
import org.kangspace.oauth2.helper.request.Response;
import org.kangspace.oauth2.helper.storage.TokenStorage;
import org.kangspace.oauth2.helper.storage.local.InMemoryTokenStorage;
import org.kangspace.oauth2.helper.storage.redis.RedisTemplateTokenStorage;
import org.kangspace.oauth2.helper.storage.redis.RedissonTokenStorage;
import org.kangspace.oauth2.helper.token.TokenResponse;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ClientCredentialsServiceTest
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Slf4j
@RunWith(JUnit4.class)
public class ClientCredentialsServiceSimpleTest {
    WeChatClientCredentialsService weChatClientCredentialsService;
    String clientId = System.getProperty("clientId");
    String clientSecret = System.getProperty("clientSecret");

    @Before
    public void init() {
        weChatClientCredentialsService = new WeChatClientCredentialsService(clientId, clientSecret);
    }

    @Test
    public void getTokenTest() {
        WeChatToken token = weChatClientCredentialsService.getToken(false);
        log.info("token: {}", token);
        WeChatToken newToken = weChatClientCredentialsService.getToken(true);
        log.info("newToken: {}", newToken);
        Assert.assertNotEquals(token, newToken);
        WeChatToken oldToken = weChatClientCredentialsService.getTokenStorage().get(clientId);
        log.info("oldToken: {}", oldToken);
    }

    @Test
    public void getApiDomainIpTest() {
        MpServerIpListResponse response = weChatClientCredentialsService.getApiDomainIp();
        log.info("response: {}", response);
    }



    /**
     * 微信客户模式授权Service <br>
     *
     * <pre>
     *
     * 实现ClientCredentialsService接口
     * 1. 重写getClientId方法，获取clientId
     * 2. 重写getClientSecret方法，获取clientSecret
     * 3. 重写getToken方法，获取token
     * </pre>
     */
    @Data
    public static class WeChatClientCredentialsService implements ClientCredentialsService<WeChatToken> {
        private String clientId;
        private String clientSecret;

        public WeChatClientCredentialsService(@NonNull String clientId, @NonNull String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        @Override
        @SneakyThrows
        public boolean tokenSet4Request(HttpRequestBase request, String token) {
            URI uri = request.getURI();
            request.setURI(new URI(UrlHelper.urlReplaceParam(uri.toString(), "access_token", token)));
            return true;
        }

        @Override
        public TokenResponse<WeChatToken> getToken(@NonNull String clientId, @NonNull String clientSecret) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + clientId
                    + "&secret=" + clientSecret;
            return get(url, getHttpHeaders(), WeChatToken.class);
        }

        /**
         * 获取微信API域名IP(需Token)
         *
         * @return MpServerIpListResponse
         */
        public MpServerIpListResponse getApiDomainIp() {
            String url = "https://api.weixin.qq.com/cgi-bin/get_api_domain_ip";
            return getWithToken(url, getHttpHeaders(), MpServerIpListResponse.class);
        }

        /**
         * 获取HttpHeaders
         *
         * @return HttpHeaders
         */
        private Map<String, String> getHttpHeaders() {
            return null;
        }
    }

    /**
     * 微信token <br>
     * 定义Token实体类, 继承DefaultToken或实现Token接口<br>
     */
    @Data
    public static class WeChatToken extends WeChatMpResponseEntity implements TokenResponse<WeChatToken> {
        @JsonProperty("access_token")
        private String accessToken;

        /**
         * 超时时间,单位s,默认: 2小时
         */
        @JsonProperty("expires_in")
        private Long expiresIn;
    }
    /**
     * 微信API响应
     */
    public static class WeChatMpResponseEntity implements Response {
        @JsonProperty("errcode")
        private Integer errcode;
        @JsonProperty("errmsg")
        private String errmsg;

        @Override
        public boolean isSucceed() {
            return errcode == null || errcode == 0;
        }

        @Override
        public boolean isInvalidToken() {
            return errcode != null && (Objects.equals(errcode, 40001) || Objects.equals(errcode, 41001) || Objects.equals(errcode, 40014) || Objects.equals(errcode, 42001));
        }
    }

    /**
     * 微信API域名IP列表响应
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MpServerIpListResponse extends WeChatMpResponseEntity {

        /**
         * 服务器Ip列表
         */
        @JsonProperty("ip_list")
        private List<String> ipList;

    }
}
