package io.geewit.weixin.event.mp.webmvc.context;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Setter
@Getter
public class EventContext<E extends WxMessage> {
    private String appId;
    private String requestXml;
    private String responseXml;

    private E requestMessage;
}
