package org.kangspace.oauth2.helper.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kangspace.devhelper.thread.ThreadUtil;
import org.kangspace.oauth2.helper.storage.local.InMemoryTokenStorage;
import org.kangspace.oauth2.helper.token.DefaultToken;
import org.kangspace.oauth2.helper.token.Token;

/**
 * InMemoryTokenStorageTest
 *
 * @author kango2gler@gmail.com
 * @since 0.0.1
 */
@Slf4j
@RunWith(JUnit4.class)
public class InMemoryTokenStorageTest {
    private InMemoryTokenStorage<DefaultToken> inMemoryTokenStorage;

    @Before
    public void setUp() {
        inMemoryTokenStorage = new InMemoryTokenStorage<>();
    }

    @Test
    public void testSetAndGet() {
        log.info("testSetAndGet");
        inMemoryTokenStorage.set("test", new DefaultToken("accessToken", 1L, "refreshToken", "tokenType"));
        Token token = inMemoryTokenStorage.get("test");
        log.info("token: {}", token);
        Assert.assertNotNull(token);
        ThreadUtil.sleep(2000);
        Token token2 = inMemoryTokenStorage.get("test");
        log.info("token2: {}", token2);
        Assert.assertNull(token2);
    }

}