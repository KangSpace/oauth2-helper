package org.kangspace.oauth2.helper.exception;

/**
 * 未授权异常
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public class UnauthorizedException extends OAuth2HelperException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
