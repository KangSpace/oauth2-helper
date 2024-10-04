package org.kangspace.oauth2.helper.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpRequest;

/**
 * 非200/201响应异常
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UnOKResponseException extends OAuth2HelperException {
    private final int code;
    private final HttpRequest request;
    private final String response;

    public UnOKResponseException(int code, HttpRequest request, String response) {
        super("请求异常: 非200/201响应, 状态码: " + code + ", 响应信息: " + response);
        this.code = code;
        this.request = request;
        this.response = response;
    }

    @Override
    public String toString() {
        return "UnOKResponseException{" +
                "code=" + code +
                ", request=" + request +
                ", response='" + response + '\'' +
                '}';
    }
}
