package io.geewit.weixin.api.common.test;

import io.geewit.weixin.api.common.COMMON;
import io.geewit.weixin.api.common.model.AccessTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;


/**
 * @author geewit
 * @since 2022-01-07
 */
@Slf4j
public class COMMONTest {

    @ParameterizedTest
    @CsvSource({
            "need_fill_in, need_fill_in, true",
            "1124855297488465920, 1124855297488465920, false",
    })
    public void testAccessToken(String appId, String secret, String expect) {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAppId(appId);
        accessTokenRequest.setSecret(secret);
        COMMON.AccessToken.INVOKER.setRequestParam(accessTokenRequest);
        COMMON.AccessToken.Response response = COMMON.AccessToken.INVOKER.invoke();
        String accessToken = response.getAccessToken();
        Assertions.assertTrue(Boolean.parseBoolean(expect) == StringUtils.isNotBlank(accessToken));
    }

    @ParameterizedTest
    @CsvSource({
            "need_fill_in, need_fill_in"
    })
    public void testUserList(String appId, String secret) {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAppId(appId);
        accessTokenRequest.setSecret(secret);
        COMMON.UserList.INVOKER.initAccessTokenParams(accessTokenRequest);
        COMMON.UserList.Request userListRequest = new COMMON.UserList.Request();
        COMMON.UserList.Response response = COMMON.UserList.INVOKER.invoke(userListRequest);
        log.info(response.toString());
    }
}
