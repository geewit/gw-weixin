package io.geewit.weixin.event.mp.webmvc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("io.geewit.weixin.event.mp.filter")
public class WxMpEventFilterProperties {
    public static final String APPID_PARAM_NAME = "appId";
    private String filterName;
    private String contextPath;
    private String listenerPath;
    private Integer order;
}
