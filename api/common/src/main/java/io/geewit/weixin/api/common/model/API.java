package io.geewit.weixin.api.common.model;

import io.geewit.weixin.api.common.utils.MapToPojoUtils;
import lombok.Builder;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author geewit
 * @since 2022-01-07
 */
public interface API<REQ extends CommonRequest, RES extends CommonResponse> {
    ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>() {
    };

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

        private AbstractHttpMessageConverter<RES> toResponseConverter() {
            final AbstractHttpMessageConverter<RES> responseConverter = new AbstractHttpMessageConverter<RES>() {
                @Override
                protected boolean supports(Class<?> clazz) {
                    return type.isAssignableFrom(clazz);
                }

                @Override
                protected RES readInternal(Class<? extends RES> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
                    try {
                        Map<String, Object> responseParameters = (Map<String, Object>) new MappingJackson2HttpMessageConverter()
                                .read(STRING_OBJECT_MAP.getType(), null, inputMessage);
                        Object errcode = responseParameters.get(API.ERRCODE);
                        if (Objects.nonNull(errcode)) {
                            throw new IllegalArgumentException("errcode：" + errcode + " errmsg：" + responseParameters.get("errmsg"));
                        }
                        RES instance = null;
                        try {
                            instance = clazz.newInstance();
                            MapToPojoUtils.mapToPojo(responseParameters, instance);
                        } catch (InstantiationException | IllegalAccessException e) {
                            logger.info(e.getMessage(), e);
                        }
                        return instance;
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
    RES invoke(REQ request);

}
