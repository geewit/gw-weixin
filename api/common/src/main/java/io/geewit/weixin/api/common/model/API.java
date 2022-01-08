package io.geewit.weixin.api.common.model;

import io.geewit.core.utils.reflection.BeanUtils;
import io.geewit.weixin.api.common.APIs;
import io.geewit.weixin.api.common.exception.WxApiException;
import io.geewit.weixin.api.common.utils.APIUtils;
import io.geewit.weixin.api.common.utils.MapToPojoUtils;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
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

    String getName();

    String getUri();

    /**
     * 请求
     * @return
     */
    HttpMethod getMethod();

    default RestTemplate getRestTemplate() {
        return APIUtils.ofRestTemplate(this);
    }

    /**
     * 调用接口
     * @param request
     * @return
     */
    default RES invoke(REQ request) {
        UriTemplate uriTemplate = new UriTemplate(this.getUri());
        if (this.getRequest().isWithToken()) {
            APIs.AccessToken.Response accessToken = APIUtils.getAccessTokenCached(APIUtils.FETCH_ACCESS_TOKEN_REQUEST);
            if (StringUtils.isBlank(accessToken.getAccessToken())) {
                throw new IllegalArgumentException("接口(" + this.getName() + ")的请求access_token不能为空");
            } else {
                request.accessToken = accessToken.getAccessToken();
            }
        } else {
            request.accessToken = null;
        }
        Map<String, ?> uriVariables = BeanUtils.pojoToMap(request);
        URI requestUri = uriTemplate.expand(uriVariables);
        HttpEntity<REQ> requestEntity = null;
        if (EnumSet.of(HttpMethod.POST, HttpMethod.PUT).contains(this.getMethod())) {
            requestEntity = new HttpEntity<>(request);
        }

        RestTemplate restTemplate = this.getRestTemplate();
        ResponseEntity<RES> responseEntity = restTemplate.exchange(requestUri, HttpMethod.valueOf(this.getMethod().name()), requestEntity, this.getResponse().getType());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new WxApiException("接口(" + this.getName() + ")的响应状态码为:" + responseEntity.getStatusCodeValue());
        }
        RES response = responseEntity.getBody();
        if (response == null) {
            throw new WxApiException("接口(" + this.getName() + ")的响应为null");
        }
        if (response.failed()) {
            throw new WxApiException("接口(" + this.getName() + ")的响应返回失败码:" + response.getErrcode());
        }
        return response;
    }
}
