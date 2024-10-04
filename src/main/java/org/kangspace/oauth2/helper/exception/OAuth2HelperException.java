package org.kangspace.oauth2.helper.exception;

/**
 * OAuth2Helper异常
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
public class OAuth2HelperException extends RuntimeException {
    public OAuth2HelperException(String message) {
        super(message);
    }

    public OAuth2HelperException(String message, Throwable cause) {
        super(message, cause);
    }
}
