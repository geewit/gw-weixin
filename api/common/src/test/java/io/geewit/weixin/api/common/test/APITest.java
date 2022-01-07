package io.geewit.weixin.api.common.test;

import io.geewit.weixin.api.common.APIs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;


/**
 * @author geewit
 * @since 2022-01-07
 */
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
        APIs.AccessToken.Response response = APIs.AccessToken.FETCH_ACCESS_TOKEN.invoke(request);
        String accessToken = response.getAccessToken();
        Assertions.assertTrue(Boolean.parseBoolean(expect) == StringUtils.isNotBlank(accessToken));
    }
}
