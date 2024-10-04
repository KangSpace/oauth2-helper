package org.kangspace.oauth2.helper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kangspace.oauth2.helper.ClientCredentialsServiceTest.MpServerIpListResponse;
import org.kangspace.oauth2.helper.ClientCredentialsServiceTest.WeChatClientCredentialsService;
import org.kangspace.oauth2.helper.ClientCredentialsServiceTest.WeChatToken;
import org.kangspace.oauth2.helper.storage.TokenStorage;
import org.kangspace.oauth2.helper.storage.redis.RedisTemplateTokenStorage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.kangspace.oauth2.helper.ClientCredentialsServiceTest.WECHAT_HTTP_CLIENT;

/**
 * ClientCredentialsServiceTest
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Slf4j
@SpringBootTest(classes = ClientCredentialsServiceRedisTemplateTest.SpringBootTestConfig.class)
@RunWith(SpringRunner.class)
public class ClientCredentialsServiceRedisTemplateTest {

    @Resource
    RedisTemplate<String, String> redisTemplate;
    WeChatClientCredentialsService weChatClientCredentialsService;
    String clientId = System.getProperty("clientId");
    String clientSecret = System.getProperty("clientSecret");

    @Before
    public void init() {
        // Spring RedisTemplate Storage
        TokenStorage<WeChatToken> tokenStorage = new WeChatTokenRedisTemplateStorage(redisTemplate);
        weChatClientCredentialsService = new WeChatClientCredentialsService(clientId, clientSecret, WECHAT_HTTP_CLIENT,
                tokenStorage);
    }

    @Test
    public void getApiDomainIpTest() {
        MpServerIpListResponse response = weChatClientCredentialsService.getApiDomainIp();
        log.info("response: {}", response);
    }

    @Configuration
    public static class SpringBootTestConfig {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName("127.0.0.1");
            redisStandaloneConfiguration.setPort(6379);
            redisStandaloneConfiguration.setDatabase(0);
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }

        @Bean
        public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            return template;
        }
    }

    /**
     * 微信Token存储 <br>
     * 定义Token存储类, 继承RedisTemplateTokenStorage或实现TokenStorage接口
     */
    public static class WeChatTokenRedisTemplateStorage extends RedisTemplateTokenStorage<WeChatToken> {
        public WeChatTokenRedisTemplateStorage(RedisTemplate redisTemplate) {
            super(redisTemplate);
        }

        @Override
        protected Class<WeChatToken> getTokenClass() {
            return WeChatToken.class;
        }
    }
}
