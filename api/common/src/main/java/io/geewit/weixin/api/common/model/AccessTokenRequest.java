package io.geewit.weixin.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


/**
 * @author geewit
 * @since 2022-01-07
 */
@SuperBuilder
@Setter
@Getter
public class AccessTokenRequest implements IRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 第三方用户唯一凭证
     */
    @JsonIgnore
    protected String appId;
    /**
     * 第三方用户唯一凭证密钥,即appsecret
     */
    @JsonIgnore
    protected String secret;
}
