package org.kangspace.oauth2.helper.token;

import lombok.*;


/**
 * 默认Token
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class DefaultToken implements Token {
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private String tokenType;
}
