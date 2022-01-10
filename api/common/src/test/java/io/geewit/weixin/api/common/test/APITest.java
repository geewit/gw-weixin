package io.geewit.weixin.api.common.test;

import io.geewit.weixin.api.common.APIs;
import io.geewit.weixin.api.common.utils.APIUtils;
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
public class APITest {

    @ParameterizedTest
    @CsvSource({
            "need_fill_in, need_fill_in, true",
            "1124855297488465920, 1124855297488465920, false",
    })
    public void testAccessToken(String appId, String secret, String expect) {
        APIs.AccessToken.Request request = new APIs.AccessToken.Request();
        request.setAppId(appId);
        request.setSecret(secret);
        APIs.AccessToken.Response response = APIs.AccessToken.INVOKER.invoke(request);
        String accessToken = response.getAccessToken();
        Assertions.assertTrue(Boolean.parseBoolean(expect) == StringUtils.isNotBlank(accessToken));
    }

    @ParameterizedTest
    @CsvSource({
            "need_fill_in, need_fill_in"
    })
    public void testUserList(String appId, String secret) {
        APIUtils.FETCH_ACCESS_TOKEN_REQUEST.setAppId(appId);
        APIUtils.FETCH_ACCESS_TOKEN_REQUEST.setSecret(secret);
        APIs.UserList.Request userListRequest = new APIs.UserList.Request();
        APIs.UserList.Response response = APIs.UserList.INVOKER.invoke(userListRequest);
        log.info(response.toString());
    }
}
