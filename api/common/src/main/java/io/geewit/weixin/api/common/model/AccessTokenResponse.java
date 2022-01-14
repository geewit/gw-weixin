package io.geewit.weixin.api.common.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取accessToken的Api请求响应定义对象
 * @author geewit
 * @since 2022-01-07
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse extends CommonResponse {
    private static final long serialVersionUID = 1L;

    public AccessTokenResponse() {
        this.loadTimestamp = System.currentTimeMillis();
    }

    @JsonProperty(value = "access_token")
    protected String accessToken;

    /**
     * 凭证有效时间，单位：秒
     */
    @JsonProperty(value = "expires_in")
    protected Integer expiresIn;

    /**
     * 获取access_token的时间, 用于和当前时间对比判断是否过期
     */
    @JsonIgnore
    protected Long loadTimestamp;

    /**
     * 判断 access_token 是否过期
     * @return true: 过期, false: 有效
     */
    public boolean expired() {
        if(loadTimestamp == null) {
            return true;
        } else {
            long expiredTimestamp = loadTimestamp + expiresIn * 1000;
            return System.currentTimeMillis() > expiredTimestamp;
        }
    }

    @Override
    public String toString() {
        return "AccessTokenResponse {"
                + "errcode=" + errcode
                + ", errmsg=" + errmsg
                + ", accessToken=" + accessToken
                + ", expiresIn=" + expiresIn
                + ", loadTimestamp=" + loadTimestamp
                + '}';
    }
}
