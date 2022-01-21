package io.geewit.weixin.event.mp.common.model;

import io.geewit.core.utils.enums.Name;

/**
 * Created by geewit on 2016/5/8.
 */
public interface MpParam extends Name {
    /**
     * 开发者微信号
     */
    String ToUserName = "ToUserName";
    /**
     * 发送方帐号（一个OpenID）
     */
    String FromUserName = "FromUserName";
    /**
     * 消息创建时间 （整型）
     */
    String CreateTime = "CreateTime";
    /**
     * 消息类型  文本为text, 图片为image, 语音为voice, 视频为video, 链接为link, 事件event
     */
    String MsgType = "MsgType";
    /**
     * 文本消息内容
     */
    String Content = "Content";
    /**
     * 图片消息 图片链接（由系统生成）
     */
    String picUrl = "picUrl";
    /**
     * 语音消息, 语音格式，如amr，speex等
     */
    String Format = "Format";
    /**
     * 媒体id，可以调用获取临时素材接口拉取数据。
     */
    String MediaId = "MediaId";
    /**
     * 语音识别结果，UTF8编码
     */
    String Recognition = "Recognition";
    /**
     * 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
     */
    String ThumbMediaId = "ThumbMediaId";
    /**
     * 消息id，64位整型
     */
    String MsgId = "MsgId";
    /**
     * 地理位置消息, 地理位置维度
     */
    String Location_X = "Location_X";
    /**
     * 地理位置消息, 地理位置经度
     */
    String Location_Y = "Location_Y";
    /**
     * 地理位置消息, 地图缩放大小
     */
    String Scale = "Scale";
    /**
     * 地理位置消息, 地理位置信息
     */
    String Label = "Label";
    /**
     *消息链接
     */
    String Url = "Url";
    /**
     * 消息标题
     */
    String Title = "Title";
    /**
     * 消息描述
     */
    String Description = "Description";
    /**
     * 事件类型
     * subscribe(订阅),
     * unsubscribe(取消订阅),
     * SCAN(用户已关注时的事件推送),
     * LOCATION(上报地理位置事件),
     * CLICK(点击菜单拉取消息时的事件)
     * VIEW(点击菜单跳转链接时的事件推送)
     */
    String Event = "Event";
    /**
     * 事件KEY值，qrscene_为前缀，后面为二维码的参数值
     */
    String EventKey = "EventKey";
    /**
     * 二维码的ticket，可用来换取二维码图片
     */
    String Ticket = "Ticket";
    /**
     * 上报地理位置事件 地理位置纬度
     */
    String Latitude = "Latitude";
    /**
     * 上报地理位置事件 地理位置经度
     */
    String Longitude = "Longitude";
    /**
     * 上报地理位置事件 地理位置精度
     */
    String Precision = "Precision";
    /**
     *
     */
    String ScanCodeInfo = "ScanCodeInfo";
    /**
     *
     */
    String ScanResult = "ScanResult";
}
