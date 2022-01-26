package io.geewit.weixin.api.common.model;

import io.geewit.core.utils.reflection.BeanUtils;
import io.geewit.weixin.api.common.exception.WxApiException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
public class AccessTokenInvoker<REQ extends AccessTokenRequest, RES extends AccessTokenResponse> implements Invoker<REQ, RES> {
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
     * 接口请求参数
     */
    private REQ requestParam;

    /**
     * access_token 失效时间 单位: 秒
     */
    private Integer expiredSeconds;

    public AccessTokenInvoker<REQ, RES> setRequestParam(REQ requestParam) {
        this.requestParam = requestParam;
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

        AccessTokenInvoker<REQ, RES> invoker = (AccessTokenInvoker<REQ, RES>) that;

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
        return "AccessTokenInvoker {"
                + "name=" + name
                + ", uri=" + uri
                + ", method=" + method
                + ", request=" + request
                + ", response=" + response
                + ", expiredSeconds=" + expiredSeconds
                + '}';
    }

    /**
     * 调用接口
     * @return 获取 access_token
     */
    public RES invoke() {
        UriTemplate uriTemplate = new UriTemplate(this.getUri());
        Map<String, ?> uriVariables = BeanUtils.pojoToMap(requestParam);
        URI requestUri = uriTemplate.expand(uriVariables);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<REQ> requestEntity;
        if (EnumSet.of(HttpMethod.POST, HttpMethod.PUT).contains(this.getMethod())) {
            requestEntity = new HttpEntity<>(requestParam, headers);
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
        Integer expiresIn = response.getExpiresIn();
        if (expiresIn != null) {
            this.expiredSeconds = expiresIn;
        }
        return response;
    }
}
