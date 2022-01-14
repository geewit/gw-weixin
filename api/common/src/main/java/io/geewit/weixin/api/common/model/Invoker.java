package io.geewit.weixin.api.common.model;

import io.geewit.web.utils.JsonUtils;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 微信接口定义的接口类
 * @author geewit
 * @since 2022-01-07
 */
public interface Invoker<REQ extends IRequest, RES extends IResponse> {
    String ACCESS_TOKEN_KEY = "access_token";
    String WEIXIN_ID = "weixin";
    String OPENID_KEY = "openid";
    String LANG_KEY = "lang";
    String DEFAULT_LANG = "zh_CN";
    String APPID_KEY = "appid";
    String SECRET_KEY = "secret";
    String ERRCODE = "errcode";
    String URI_SUFFIX = "#wechat_redirect";

    @Builder
    @Getter
    class Request<REQ extends IRequest> {
        @Builder.Default
        private MediaType mediaType = MediaType.APPLICATION_JSON;
        private Class<REQ> type;

        @Override
        public String toString() {
            return "Invoker.Request {"
                    + "mediaType=" + mediaType
                    + ", type=" + type.getName()
                    + '}';
        }
    }

    @Builder
    @Getter
    class Response<RES extends IResponse> {
        /**
         * 接口响应的 mediaType
         */
        private MediaType mediaType;

        /**
         * 接口响应的 responseBody 解析的对象 class
         */
        private Class<RES> type;

        /**
         * 接口响应期望的 response code
         */
        @Builder.Default
        private int expectCode = HttpStatus.OK.value();

        private AbstractHttpMessageConverter<RES> toResponseConverter() {
            final AbstractHttpMessageConverter<RES> responseConverter = new AbstractHttpMessageConverter<RES>() {
                @Override
                protected boolean supports(Class<?> clazz) {
                    return type.isAssignableFrom(clazz);
                }

                @Override
                protected RES readInternal(Class<? extends RES> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
                    try {
                        if (CommonResponse.class.isAssignableFrom(clazz)) {
                            MediaType mediaType = inputMessage.getHeaders().getContentType();
                            Charset charset;
                            if (mediaType != null && mediaType.getCharset() != null) {
                                charset = mediaType.getCharset();
                            } else {
                                charset = StandardCharsets.UTF_8;
                            }

                            String responseBody = new BufferedReader(new InputStreamReader(inputMessage.getBody(), charset))
                                    .lines().parallel().collect(Collectors.joining("\n"));
                            logger.info("responseBody: " + responseBody);
                            RES instance = JsonUtils.fromJson(responseBody, type);
                            if (((CommonResponse)instance).failed()) {
                                throw new IllegalArgumentException("errcode: " + ((CommonResponse)instance).getErrcode() + " errmsg: " + ((CommonResponse)instance).getErrmsg());
                            }
                            return instance;
                        } else if (RedirectResponse.class.isAssignableFrom(clazz)) {
                            RES instance = clazz.newInstance();
                            URI location = inputMessage.getHeaders().getLocation();
                            if (location != null) {
                                ((RedirectResponse)instance).setLocation(location.toString());
                            }
                            return instance;
                        } else {
                            throw new IllegalArgumentException("接口定义出错");
                        }

                    } catch (Exception ex) {
                        throw new HttpMessageNotReadableException(
                                "An error occurred reading the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex,
                                inputMessage);
                    }
                }

                @Override
                protected void writeInternal(RES res, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
                }
            };

            responseConverter.setDefaultCharset(StandardCharsets.UTF_8);
            responseConverter.setSupportedMediaTypes(
                    Stream.of(
                            MediaType.APPLICATION_JSON_UTF8,
                            MediaType.TEXT_PLAIN,
                            MediaType.TEXT_HTML,
                            new MediaType(MediaType.APPLICATION_JSON.getType(), "*+json")
                    ).collect(Collectors.toList()));
            return responseConverter;
        }
        private final AbstractHttpMessageConverter<RES> responseConverter = this.toResponseConverter();

        @Override
        public String toString() {
            return "Invoker.Response {"
                    + "mediaType=" + mediaType
                    + ", type=" + type
                    + ", expectCode=" + expectCode
                    + '}';
        }
    }

    Request<REQ> getRequest();

    Response<RES> getResponse();

    String getName();

    String getUri();

    /**
     * 请求
     * @return
     */
    HttpMethod getMethod();

    default RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(Collections.singletonList(this.getResponse().getResponseConverter()));
        return restTemplate;
    }
}
