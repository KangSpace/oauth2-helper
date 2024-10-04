package org.kangspace.oauth2.helper.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 非正常业务成功响应异常
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Getter
@Setter
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = true)
public class UnSucceedResponseException extends OAuth2HelperException {
    private final String response;

    public UnSucceedResponseException(String response) {
        super("请求异常: 非正常业务成功响应异常, 响应信息: " + response);
        this.response = response;
    }

    @Override
    public String toString() {
        return "UnSucceedResponseException{" +
                ", response='" + response + '\'' +
                '}';
    }
}
