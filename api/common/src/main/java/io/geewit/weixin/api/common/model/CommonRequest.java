package io.geewit.weixin.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author geewit
 * @since 2022-01-07
 */
@Setter
@Getter
public abstract class CommonRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    protected String accessToken;
}
