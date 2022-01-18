package io.geewit.weixin.sns.api;

import io.geewit.weixin.api.common.COMMON;
import io.geewit.weixin.api.common.model.AccessTokenRequest;
import io.geewit.weixin.api.sns.SNS;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


/**
 * @author geewit
 * @since 2022-01-07
 */
@Slf4j
public class SNSTest {

    @ParameterizedTest
    @CsvSource({
            "need_fill_in",
    })
    public void testOauth2Code(String appId) {
        SNS.Login.OAuth2Code.Request request = SNS.Login.OAuth2Code.Request.builder().appId(appId).redirectUri("https://www.geewit.io").build();
        request.setScope(SNS.Login.OAuth2Code.RequestScope.snsapi_base);
        request.setState("A01");
        SNS.Login.OAuth2Code.Response response = SNS.Login.OAuth2Code.INVOKER.invoke(request);
        log.info("response.location = {}", response.getLocation());
    }

    @ParameterizedTest
    @CsvSource({
            "need_fill_in, need_fill_in"
    })
    public void testUserList(String appId, String secret) {
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder().appId(appId).secret(secret).build();
        COMMON.UserList.INVOKER.initAccessTokenParams(accessTokenRequest);
        COMMON.UserList.Request userListRequest = COMMON.UserList.Request.builder().build();
        COMMON.UserList.Response response = COMMON.UserList.INVOKER.invoke(userListRequest);
        log.info(response.toString());
    }
}
