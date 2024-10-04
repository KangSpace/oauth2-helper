# oauth2-helper

⚒️ oAuth2 utility tools. Encapsulate oAuth2 HTTP request token maintenance. ⚒️oAuth2工具类封装。封装oAuth2 Http请求Token维护。

## 功能

1. 封装 oAuth2 鉴权请求

2. 维护 oAuth2 请求 Token

3. 支持多种 oAuth2 认证类型

## 快速开始

依赖：

> 最新版本号： 0.0.1

```xml
<dependency>
    <groupId>com.github.kangspace</groupId>
    <artifactId>oauth2-helper</artifactId>
    <version>{lastest.version}</version>
</dependency>
```

### 使用

样例：

> 以微信公众号请求为例, 详细代码见`ClientCredentialsServiceTest`类

```java
// 1. 实现 ClientCredentialsService (客户端模式)
// a. 定义Token类
// 实现TokenResponse接口, 重写isSucceed, isInvalidToken方法

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


// b. 定义clientId, clientSecret属性，并实现tokenSet4Request, getToken方法
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
            return get(url, null, WeChatToken.class);
        }

        /**
         * 获取微信API域名IP(需Token)
         *
         * @return MpServerIpListResponse
         */
        public MpServerIpListResponse getApiDomainIp() {
            String url = "https://api.weixin.qq.com/cgi-bin/get_api_domain_ip";
            return getWithToken(url, null, MpServerIpListResponse.class);
        }
    }

// 2. 使用

WeChatClientCredentialsService weChatClientCredentialsService = new WeChatClientCredentialsService(clientId, clientSecret);

// 3. 调用Service方法

MpServerIpListResponse response = weChatClientCredentialsService.getApiDomainIp();

```

### 说明

#### 1. 定义Token类
Token类需实现TokenResponse接口, 重写isSucceed, isInvalidToken方法，并实现clientId, clientSecret等属性

> 因为Token请求可能会失败，失败时返回的是code,message, 成功时返回的是token

| 属性/方法名 | 类型(属性/方法) | 是否必选 | 说明 |
|--------|----------|------|------|
| accessToken | 属性 | 是 | 访问令牌 |
| expiresIn | 属性 | 是 | 凭证有效时间，单位：秒 |
| refreshToken | 属性 | 否 | 刷新令牌 |
| tokenType | 属性 | 否 | 令牌类型 |
| isSucceed  | 方法 | 是 | 请求是否成功，一般判断code码为null或成功的code码 |
| isInvalidToken | 方法 | 是 | 请求是否失败，且失败原因是否是Token失效 |


#### 2. 定义ClientCredentialsService 或 PasswordService
> ClientCredentialsService 为客户端模式，PasswordService 为密码模式，区别在于客户端模式是直接使用clientId, clientSecret请求，密码模式是使用用户名，密码请求，核心处理逻辑类似

ClientCredentialsService 存在以下必选/可选属性

| 属性/方法名 | 类型(属性/方法) | 是否必选 | 说明 |
|--------|----------|------|------| 
| clientId | 属性 | 是 | 客户端ID |
| clientSecret | 属性 | 是 | 客户端密钥 |
| httpClient | 属性 | 否 | Http请求客户端，默认值为：`RequestFactory.getHttpClient()`, 具体见`RequestFactory` |
| tokenStorage | 属性 | 否 | Token存储，默认值为：`InMemoryTokenStorage`，内存缓存，建议在生产环境使用Redis等分布式缓存；内置实现了`RedissonTokenStorage`,`RedisTemplateTokenStorage`,`InMemoryTokenStorage`3种存储方式 |
| useRefreshToken | 属性 | 否 | 是否使用refresh_token来刷新新token，默认值为：`false`，为true时需实现`refreshToken`方法 |
| tokenSet4Request | 方法 | 是 | 设置Token到请求中，默认为请求头：`Authorization: Bearer {token}`，有些场景下token在url中，则需要重写此方法，参考`ClientCredentialsServiceTest`类的实现 |
| getToken(String clientId, String clientSecret) | 方法 | 是 | 实际需要获取Token的方法，参考`ClientCredentialsServiceTest`类的实现 |
| refreshToken | 方法 | 否 | 刷新Token，当`useRefreshToken`为true时，需实现此方法用于通过refresh_token刷新新token |

ClientCredentialsService 中内置了Http的`GET,POST,PUT,DELETE`方法及其需要Token的请求方法，具体见`OAuth2RequestService`类

可在实现的Service中直接使用`get,post,put,delete`方法请求，也可使用`getWithToken,postWithToken,putWithToken,deleteWithToken`方法请求，具体见`OAuth2RequestService`类。

如：
```java
    /**
     * 获取微信API域名IP(需Token)
     *
     * @return MpServerIpListResponse
     */
    public MpServerIpListResponse getApiDomainIp() {
        String url = "https://api.weixin.qq.com/cgi-bin/get_api_domain_ip";
        return getWithToken(url, getHttpHeaders(), MpServerIpListResponse.class);
    }

```


##### 注意

tokenStorage 默认为 `InMemoryTokenStorage`，只在当前实例生效，可用于本地测试，在生产环境中，建议使用 `RedisTokenStorage` 或 `RedissonTokenStorage` 等分布式缓存

