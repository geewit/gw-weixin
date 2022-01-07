package io.geewit.weixin.api.common.model;

import io.geewit.core.utils.reflection.BeanUtils;
import io.geewit.weixin.api.common.exception.WxApiException;
import io.geewit.weixin.api.common.APIs;
import io.geewit.weixin.api.common.utils.APIUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Objects;


/**
 * 微信API 定义对象
 * @author geewit
**/
@Builder
@Setter
@Getter
public class WeixinAPI<REQ extends CommonRequest, RES extends CommonResponse> implements API<REQ, RES> {
    private WeixinAPI() {
    }

    private String name;
    private String uri;
    private HttpMethod method;
    private Request<REQ> request;
    private Response<RES> response;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        WeixinAPI weixinAPI = (WeixinAPI) that;

        if (this.method != weixinAPI.method) {
            return false;
        }
        return Objects.equals(uri, weixinAPI.uri);
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WeixinAPI{" +
                "name=" + name +
                ", uri=" + uri +
                ", method=" + method +
                ", request=" + request +
                ", response=" + response +
                '}';
    }

    @Override
    public RES invoke(RestTemplate restTemplate, REQ request) {
        UriTemplate uriTemplate = new UriTemplate(this.uri);
        if (this.request.isWithToken()) {
            APIs.AccessToken.Response accessToken = APIUtils.getAccessTokenCached(APIUtils.FETCH_ACCESS_TOKEN_REQUEST);
            if (StringUtils.isBlank(accessToken.getAccessToken())) {
                throw new IllegalArgumentException("接口(" + this.name + ")的请求access_token不能为空");
            } else {
                request.accessToken = accessToken.getAccessToken();
            }
        } else {
            request.accessToken = null;
        }
        Map<String, ?> uriVariables = BeanUtils.pojoToMap(request);
        URI requestUri = uriTemplate.expand(uriVariables);
        HttpEntity<REQ> requestEntity = new HttpEntity<>(request);

        ResponseEntity<RES> responseEntity = restTemplate.exchange(requestUri, HttpMethod.valueOf(this.method.name()), requestEntity, response.getType());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new WxApiException("接口(" + this.name + ")的响应状态码为:" + responseEntity.getStatusCodeValue());
        }
        RES response = responseEntity.getBody();
        if (response == null) {
            throw new WxApiException("接口(" + this.name + ")的响应为null");
        }
        if (response.failed()) {
            throw new WxApiException("接口(" + this.name + ")的响应返回失败码:" + response.getErrcode());
        }
        return response;
    }
}
