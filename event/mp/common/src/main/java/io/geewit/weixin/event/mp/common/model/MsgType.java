package io.geewit.weixin.event.mp.common.model;

import io.geewit.core.utils.enums.Name;

public enum MsgType implements Name {
    text,
    image,
    link,
    location,
    news,
    voice,
    video,
    shortvideo,
    event,
    /**
     * 消息转发到指定客服
     */
    transfer_customer_service
}