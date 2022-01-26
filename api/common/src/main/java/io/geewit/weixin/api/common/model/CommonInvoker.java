package io.geewit.weixin.api.common.model;

import io.geewit.core.utils.reflection.BeanUtils;
import io.geewit.weixin.api.common.exception.WxApiException;
import io.geewit.weixin.api.common.utils.AccessTokenUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;


/**
 * 微信API 定义对象
 * @author geewit
**/
@Builder
@Setter
@Getter
public class CommonInvoker<REQ extends CommonRequest, RES extends CommonResponse> implements Invoker<REQ, RES> {
    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口 uri template
     */
    private String uri;
    /**
     * 接口 请求方法
     */
    private HttpMethod method;

    /**
     * 接口请求 对象
     */
    private Request<REQ> request;

    /**
     * 接口响应 对象
     */
    private Response<RES> response;

    /**
     * 获取 access_token 的 invoker
     */
    private AccessTokenInvoker<? super AccessTokenRequest, ? extends AccessTokenResponse> tokenInvoker;

    /**
     * 初始化设置 获取 access_token 的参数
     * @param request 获取 access_token 的参数
     */
    public <AT_REQ extends AccessTokenRequest> CommonInvoker<REQ, RES> install(AT_REQ request) {
        this.tokenInvoker.setRequestParam(request);
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        CommonInvoker<REQ, RES> invoker = (CommonInvoker<REQ, RES>) that;

        if (this.method != invoker.method) {
            return false;
        }
        return Objects.equals(uri, invoker.uri);
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommonInvoker {" +
                "name=" + name +
                ", uri=" + uri +
                ", method=" + method +
                ", request=" + request +
                ", response=" + response +
                ", tokenInvoker=" + tokenInvoker.getName() +
                '}';
    }

    /**
     * 根据请求参数渲染请求URI
     * @param request 请求参数
     * @return 请求URI
     */
    public URI renderUri(REQ request) {
        UriTemplate uriTemplate = new UriTemplate(this.getUri());
        if (request.accessToken == null) {
            if (this.tokenInvoker == null) {
                throw new IllegalArgumentException("没有配置tokenInvoker");
            }
            AccessTokenResponse accessTokenResponse = AccessTokenUtils.getAccessTokenCached(this.tokenInvoker);
            if (StringUtils.isBlank(accessTokenResponse.getAccessToken())) {
                throw new IllegalArgumentException("接口(" + this.getName() + ")的请求access_token不能为空");
            } else {
                request.accessToken = accessTokenResponse.getAccessToken();
            }
        }

        Map<String, ?> uriVariables = BeanUtils.pojoToMap(request);
        URI requestUri = uriTemplate.expand(uriVariables);
        return requestUri;
    }

    /**
     * 调用接口
     * @param request
     * @return
     */
    public RES invoke(REQ request) {
        URI requestUri = this.renderUri(request);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<REQ> requestEntity;
        if (EnumSet.of(HttpMethod.POST, HttpMethod.PUT).contains(this.getMethod())) {
            requestEntity = new HttpEntity<>(request, headers);
        } else {
            requestEntity = new HttpEntity<>(headers);
        }

        RestTemplate restTemplate = this.getRestTemplate();
        ResponseEntity<RES> responseEntity = restTemplate.exchange(requestUri, HttpMethod.valueOf(this.getMethod().name()), requestEntity, this.getResponse().getType());

        if (responseEntity.getStatusCode().value() != this.getResponse().getExpectCode()) {
            throw new WxApiException("接口(" + this.getName() + ")的响应状态码为:" + responseEntity.getStatusCodeValue() + "与期望的" + this.getResponse().getExpectCode() + "不符");
        }
        RES response = responseEntity.getBody();
        if (response == null) {
            throw new WxApiException("接口(" + this.getName() + ")的响应为null");
        }
        return response;
    }
}
