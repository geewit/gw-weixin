package io.geewit.weixin.api.common.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;

/**
 * @author geewit
 * @since 2022-01-07
 */
@Setter
public class RedirectResponse implements IResponse {
    private static final long serialVersionUID = 1L;

    public RedirectResponse() {
    }

    public RedirectResponse(String location) {
        this.location = location;
    }

    private String location;

    @JsonIgnore
    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "RedirectResponse{" +
                "location=" + location +
                '}';
    }
}
