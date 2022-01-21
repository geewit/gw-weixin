package io.geewit.weixin.event.mp.webmvc.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.weixin.event.mp.common.model.Event;
import io.geewit.weixin.event.mp.common.model.MpParam;
import io.geewit.weixin.event.mp.webmvc.context.WxMessage;

/**
 * 事件推送
 * @author geewit
 * @since 2022-01-21
 */
public abstract class EventMessage extends WxMessage {
    @JsonProperty(MpParam.Event)
    protected Event event = Event.subscribe;
}
