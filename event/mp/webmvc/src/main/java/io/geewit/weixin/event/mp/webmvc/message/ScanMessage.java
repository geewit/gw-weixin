package io.geewit.weixin.event.mp.webmvc.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.geewit.weixin.event.mp.common.model.Event;
import io.geewit.weixin.event.mp.common.model.MpParam;
import io.geewit.weixin.event.mp.webmvc.context.WxMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * 扫描带参数二维码事件 用户已关注时的事件推送
 * @author geewit
 * @since 2022-01-21
 */
@Setter
@Getter
@JacksonXmlRootElement(localName = "xml")
public class ScanMessage extends EventMessage {
    public ScanMessage() {
        super();
        super.event = Event.SCAN;
    }

    @JsonProperty(MpParam.EventKey)
    private String eventKey;

    @JsonProperty(MpParam.Ticket)
    private String ticket;
}
