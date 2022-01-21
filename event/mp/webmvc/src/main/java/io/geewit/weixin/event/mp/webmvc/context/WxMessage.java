package io.geewit.weixin.event.mp.webmvc.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import io.geewit.web.utils.XmlUtils;
import io.geewit.weixin.event.mp.common.model.Event;
import io.geewit.weixin.event.mp.common.model.MpParam;
import io.geewit.weixin.event.mp.common.model.MsgType;
import io.geewit.weixin.event.mp.common.utils.XmlToMapUtils;
import io.geewit.weixin.event.mp.webmvc.message.ScanMessage;
import io.geewit.weixin.event.mp.webmvc.message.SubscribeMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 微信推送信息
 * @author geewit
 * @since 2022-01-21
 */
@Slf4j
@Setter
@Getter
public abstract class WxMessage implements Serializable {

    /**
     * 开发者微信号
     */
    @JacksonXmlCData
    @JsonProperty(MpParam.ToUserName)
    protected String toUserName;

    /**
     * 发送方帐号（一个OpenID）
     */
    @JacksonXmlCData
    @JsonProperty(MpParam.FromUserName)
    protected String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    @JsonProperty(MpParam.CreateTime)
    protected Integer createTime;

    /**
     * 消息类型
     */
    @JacksonXmlCData
    @JsonProperty(MpParam.MsgType)
    protected MsgType msgType;

    /**
     * xml 解析到 WxMessage 对象
     * @param xml 请求报文
     * @param <E> WxMessage 对象
     * @return WxMessage 对象
     */
    public static <E extends WxMessage> E parse(String xml, Charset charset) {
        Map<String, Object> objectMap = XmlToMapUtils.xmlToMap(xml, charset);
        String msgTypeStr = (String)objectMap.get(MpParam.MsgType);
        MsgType msgType;
        try {
            msgType = MsgType.valueOf(msgTypeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("MsgType(" + msgTypeStr + ")无法识别");
        }
        switch (msgType) {
            case event: {
                Event event;
                String eventStr = (String)objectMap.get(MpParam.Event);
                try {
                    event = Event.valueOf(eventStr);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Event(" + eventStr + ")无法识别");
                }
                switch (event) {
                    case subscribe: {
                        try {
                            SubscribeMessage message = XmlUtils.fromXml(xml, SubscribeMessage.class);
                            return (E)message;
                        } catch (RuntimeException e) {
                            log.warn(e.getMessage());
                            throw new IllegalArgumentException("xml解析错误:" + e.getMessage());
                        }
                    }
                    case SCAN: {
                        try {
                            ScanMessage message = XmlUtils.fromXml(xml, ScanMessage.class);
                            return (E)message;
                        } catch (RuntimeException e) {
                            log.warn(e.getMessage());
                            throw new IllegalArgumentException("xml解析错误:" + e.getMessage());
                        }
                    }
                    default: {
                        throw new IllegalArgumentException("Event(" + event + ")目前无法处理");
                    }
                }
            }
            default: {
                throw new IllegalArgumentException("MsgType(" + msgType + ")目前无法处理");
            }
        }
    }
}
