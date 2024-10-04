# oAuth2 认证类型

> 参考: https://www.cnblogs.com/lvjinlin/p/18252116

## 1. 密码模式(password)

使用用户名/密码作为授权方式从授权服务器上获取令牌，一般不支持刷新令牌。

请求样例:
<pre>
<code> 
https://oauth.example.com/token?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&grant_type=password&username=USERNAME&password=PASSWORD
 </code>
</pre>

| 参数            | 说明                | 是否必填 |
|---------------|-------------------|------|
| client_id     | 客户端ID             | 是    |
| client_secret | 客户端密钥             | 是    |
| grant_type    | 授权类型，固定为 password | 是    |
| username      | 用户名               | 是    |
| password      | 密码                | 是    |

响应样例:
<pre>
<code>
{
    "access_token": "ACCESS_TOKEN",
    "token_type": "Bearer",
    "expires_in": 3600
}
</code>
</pre>

## 2. 客户端模式(client credentials)

客户端向服务端申请授权， 服务提供者验证通过以后，直接返回令牌，和刷新令牌，可在有效期内使用使用刷新令牌刷新令牌。

这种方式给出的令牌，是针对第三方应用的，而不是针对用户的，即有可能多个用户共享同一个令牌

请求样例:
<pre>
<code> 
https://oauth.example.com/token?grant_type=client_credentials&client_id=CLIENT_ID&client_secret=CLIENT_SECRET
 </code>
</pre>

| 参数            | 说明                          | 是否必填 |
|---------------|-----------------------------|------|
| client_id     | 客户端ID                       | 是    |
| client_secret | 客户端密钥                       | 是    |
| grant_type    | 授权类型，固定为 client_credentials | 是    |

响应样例:
<pre>
<code>
{
    "access_token": "ACCESS_TOKEN",
    "token_type": "Bearer",
    "expires_in": 3600
}
</code>
</pre>

## 3. 授权码模式(authorization-code)

第三方应用先申请一个授权码code,然后通过code获取令牌Access Token

授权码模式是功能最完全，流程最严密安全的授权模式。他的特点就是通过客户端的后台服务器，与“服务提供商”的认证服务器进行互动，access_token不会经过浏览器或移动端的App，而是直接从服务端去校验，这样就最大限度的减少了令牌泄露的风险。

## 4. 隐藏式(implicit)

首先用户访问页面时，会重定向到认证服务器，接着认证服务器给用户一个认证页面，等待用户授权，用户填写信息完成授权后，认证服务器返回token。

请求样例:

步骤1: A网站提供一个链接，用户点击后就会跳转到B网站，授权用户数据给A网站使用。下面就是A网站跳转B网站的一个示意链接：
<pre>
<code>
https://b.com/oauth/authorize?client_id=p2pweb&response_type=token&scope=app&redirect_uri=http://xx.xx/callback
</code>
</pre>

步骤2：用户跳转后，B网站会要求用户登录，然后询问是否同意给予A网站授权（此过程也可以自动授权，用户无感知）

步骤3：授权服务器将授权码将令牌（access_token）以Hash的形式存放在重定向uri的fargment中发送给浏览器。认证服务器回应客户端的URI,包含以下参数：

| 参数           | 说明                       | 是否必填 |
|--------------|--------------------------|------|
| access_token | 访问令牌                     | 是    |
| token_type   | 令牌类型，通常为"Bearer"         | 是    |
| expires_in   | 令牌有效期，以秒为单位              | 否    |
| scope        | 令牌授权范围                   | 否    |
| state        | 客户端在请求中提供的状态值，用于防止CSRF攻击 | 否    |

示例：

<pre>
<code>
https://server.example.com/cb#access_token=ACCESS_TOKEN&state=xyz&token_type=example&expires_in=3600
</code>
</pre>
或
<pre>
<code>
https://a.com/callback#token=ACCESS_TOKEN
</code>
</pre>
