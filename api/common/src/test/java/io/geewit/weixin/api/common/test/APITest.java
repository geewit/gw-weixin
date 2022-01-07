package io.geewit.weixin.api.common.test;

import io.geewit.weixin.api.common.APIs;
import io.geewit.weixin.api.common.utils.APIUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.web.client.RestTemplate;


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
        RestTemplate restTemplate = APIUtils.ofRestTemplate(APIs.AccessToken.Response.class);
        APIs.AccessToken.Request request = new APIs.AccessToken.Request();
        request.setAppId(appId);
        request.setSecret(secret);
        APIs.AccessToken.Response response = APIs.AccessToken.FETCH_ACCESS_TOKEN.invoke(restTemplate, request);
        String accessToken = response.getAccessToken();
        Assertions.assertTrue(Boolean.parseBoolean(expect) == StringUtils.isNotBlank(accessToken));
    }
}
