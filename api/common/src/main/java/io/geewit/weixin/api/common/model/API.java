package io.geewit.weixin.api.common.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.StringJoiner;

/**
 * @author geewit
 * @since 2022-01-07
 */
public interface API<REQ extends CommonRequest, RES extends CommonResponse> {
    String ACCESS_TOKEN_KEY = "access_token";
    String WECHAT_ID = "wechat";
    String OPENID_KEY = "openid";
    String LANG_KEY = "lang";
    String DEFAULT_LANG = "zh_CN";
    String APPID_KEY = "appid";
    String SECRET_KEY = "secret";
    String ERRCODE = "errcode";

    @Builder
    @Getter
    class Request<REQ extends CommonRequest> {
        private MediaType mediaType;
        private Class<REQ> type;
        @Builder.Default
        private boolean withToken = false;

        @Override
        public String toString() {
            return new StringJoiner(", ", Request.class.getSimpleName() + "[", "]")
                    .add("mediaType=" + mediaType)
                    .add("type=" + type)
                    .add("withToken=" + withToken)
                    .toString();
        }
    }

    @Builder
    @Getter
    class Response<RES extends CommonResponse> {
        private MediaType mediaType;
        private Class<RES> type;

        @Override
        public String toString() {
            return new StringJoiner(", ", Response.class.getSimpleName() + "[", "]")
                    .add("mediaType=" + mediaType)
                    .add("type=" + type)
                    .toString();
        }
    }

    Request<REQ> getRequest();

    Response<RES> getResponse();

    /**
     * 请求
     * @return
     */
    HttpMethod getMethod();

    /**
     * 调用接口
     * @param restTemplate
     * @param request
     * @return
     */
    RES invoke(RestTemplate restTemplate, REQ request);

}
