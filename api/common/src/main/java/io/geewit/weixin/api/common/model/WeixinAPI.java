package io.geewit.weixin.api.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import java.util.Objects;


/**
 * 微信API 定义对象
 * @author geewit
**/
@Builder
@Setter
@Getter
public class WeixinAPI<REQ extends CommonRequest, RES extends CommonResponse> implements API<REQ, RES> {
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
}
