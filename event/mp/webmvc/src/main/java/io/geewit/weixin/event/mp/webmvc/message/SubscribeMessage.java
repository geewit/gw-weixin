package io.geewit.weixin.event.mp.webmvc.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.geewit.weixin.event.mp.common.model.Event;
import io.geewit.weixin.event.mp.common.model.MpParam;
import io.geewit.weixin.event.mp.webmvc.context.WxMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户未关注时，进行关注后的事件推送
 */
@Setter
@Getter
@JacksonXmlRootElement(localName = "xml")
public class SubscribeMessage extends EventMessage {
    public SubscribeMessage() {
        super();
        super.event = Event.subscribe;
    }

    @JsonProperty(MpParam.EventKey)
    private String eventKey;

    @JsonProperty(MpParam.Ticket)
    private String ticket;
}
