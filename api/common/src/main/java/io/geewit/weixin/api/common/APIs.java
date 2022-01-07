package io.geewit.weixin.api.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.weixin.api.common.model.API;
import io.geewit.weixin.api.common.model.CommonRequest;
import io.geewit.weixin.api.common.model.CommonResponse;
import io.geewit.weixin.api.common.model.WeixinAPI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * @author geewit
 * @since 2022-01-07
 */
public interface APIs {


    /**
     * 获取 AccessToken 接口定义
     */
    interface AccessToken {
        WeixinAPI<Request, Response> FETCH_ACCESS_TOKEN = WeixinAPI.<Request, Response>builder()
                .name("获取AccessToken")
                .uri("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={secret}")
                .method(HttpMethod.GET)
                .request(API.Request.<Request>builder().type(AccessToken.Request.class).build())
                .response(API.Response.<Response>builder().mediaType(MediaType.APPLICATION_JSON).build())
                .build();

        @Setter
        @Getter
        class Request extends CommonRequest {
            /**
             * 第三方用户唯一凭证
             */
            @JsonIgnore
            private String appId;
            /**
             * 第三方用户唯一凭证密钥，即appsecret
             */
            @JsonIgnore
            private String secret;

            @Override
            public String toString() {
                return "FetchAccessToken.Request {" +
                        "appId=" + appId +
                        ", secret=" + secret +
                        '}';
            }
        }

        @Setter
        @Getter
        class Response extends CommonResponse {
            @JsonProperty(value = "access_token")
            private String accessToken;

            @JsonProperty(value = "expires_in")
            private Integer expiresIn;

            @JsonIgnore
            private Long loadTimestamp;

            public boolean expired() {
                if(loadTimestamp == null) {//过期
                    return true;
                } else {
                    long expiredTimestamp = loadTimestamp + expiresIn * 1000;
                    return System.currentTimeMillis() > expiredTimestamp;
                }
            }

            @Override
            public String toString() {
                return "AccessToken.Response {" +
                        "accessToken=" + accessToken +
                        ", expiresIn=" + expiresIn +
                        ", loadTimestamp=" + loadTimestamp +
                        '}';
            }
        }
    }
}
