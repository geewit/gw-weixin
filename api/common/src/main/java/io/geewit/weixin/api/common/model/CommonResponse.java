package io.geewit.weixin.api.common.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author geewit
 * @since 2022-01-07
 */
@Setter
public class CommonResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static CommonResponse SYSTEM_BUSY = new CommonResponse(-1, "系统繁忙");
    public static CommonResponse CODE_0     = new CommonResponse(0, "请求成功");
    public static CommonResponse CODE_40001 = new CommonResponse(40001, "验证失败");
    public static CommonResponse CODE_40002 = new CommonResponse(40002, "不合法的凭证类型");
    public static CommonResponse CODE_40003 = new CommonResponse(40003, "不合法的OpenID");
    public static CommonResponse CODE_40004 = new CommonResponse(40004, "不合法的媒体文件类型");
    public static CommonResponse CODE_40005 = new CommonResponse(40005, "不合法的文件类型");
    public static CommonResponse CODE_40006 = new CommonResponse(40006, "不合法的文件大小");
    public static CommonResponse CODE_40007 = new CommonResponse(40007, "不合法的媒体文件id");
    public static CommonResponse CODE_40008 = new CommonResponse(40008, "不合法的消息类型");
    public static CommonResponse CODE_40009 = new CommonResponse(40009, "不合法的图片文件大小");
    public static CommonResponse CODE_40010 = new CommonResponse(40010, "不合法的语音文件大小");
    public static CommonResponse CODE_40011 = new CommonResponse(40011, "不合法的视频文件大小");
    public static CommonResponse CODE_40012 = new CommonResponse(40012, "不合法的缩略图文件大小");
    public static CommonResponse CODE_40013 = new CommonResponse(40013, "不合法的APPID");
    public static CommonResponse CODE_40014 = new CommonResponse(40014, "不合法的access_token");
    public static CommonResponse CODE_40015 = new CommonResponse(40015, "不合法的菜单类型");
    public static CommonResponse CODE_40016 = new CommonResponse(40016, "不合法的按钮个数");
    public static CommonResponse CODE_40017 = new CommonResponse(40017, "不合法的按钮个数");
    public static CommonResponse CODE_40018 = new CommonResponse(40018, "不合法的按钮名字长度");
    public static CommonResponse CODE_40019 = new CommonResponse(40019, "不合法的按钮KEY长度");
    public static CommonResponse CODE_40020 = new CommonResponse(40020, "不合法的按钮URL长度");
    public static CommonResponse CODE_40021 = new CommonResponse(40021, "不合法的菜单版本号");
    public static CommonResponse CODE_40022 = new CommonResponse(40022, "不合法的子菜单级数");
    public static CommonResponse CODE_40023 = new CommonResponse(40023, "不合法的子菜单按钮个数");
    public static CommonResponse CODE_40024 = new CommonResponse(40024, "不合法的子菜单按钮类型");
    public static CommonResponse CODE_40025 = new CommonResponse(40025, "不合法的子菜单按钮名字长度");
    public static CommonResponse CODE_40026 = new CommonResponse(40026, "不合法的子菜单按钮KEY长度");
    public static CommonResponse CODE_40027 = new CommonResponse(40027, "不合法的子菜单按钮URL长度");
    public static CommonResponse CODE_40028 = new CommonResponse(40028, "不合法的自定义菜单使用用户");
    public static CommonResponse CODE_40029 = new CommonResponse(40029, "不合法的oauth_code");
    public static CommonResponse CODE_40030 = new CommonResponse(40030, "不合法的refresh_token");
    public static CommonResponse CODE_40031 = new CommonResponse(40031, "不合法的openid列表");
    public static CommonResponse CODE_40032 = new CommonResponse(40032, "不合法的openid列表长度");
    public static CommonResponse CODE_40033 = new CommonResponse(40033, "不合法的请求字符，不能包含\\uxxxx格式的字符");
    public static CommonResponse CODE_40034 = new CommonResponse(40034, "不合法的模板大小");
    public static CommonResponse CODE_40035 = new CommonResponse(40035, "不合法的参数");
    public static CommonResponse CODE_40036 = new CommonResponse(40036, "不合法的模板id大小");
    public static CommonResponse CODE_40037 = new CommonResponse(40037, "不合法的模板id");
    public static CommonResponse CODE_40038 = new CommonResponse(40038, "不合法的请求格式");
    public static CommonResponse CODE_40039 = new CommonResponse(40039, "不合法的URL长度");
    public static CommonResponse CODE_41001 = new CommonResponse(41001, "缺少access_token参数");
    public static CommonResponse CODE_41002 = new CommonResponse(41002, "缺少appid参数");
    public static CommonResponse CODE_41003 = new CommonResponse(41003, "缺少refresh_token参数");
    public static CommonResponse CODE_41004 = new CommonResponse(41004, "缺少secret参数");
    public static CommonResponse CODE_41005 = new CommonResponse(41005, "缺少多媒体文件数据");
    public static CommonResponse CODE_41006 = new CommonResponse(41006, "缺少media_id参数");
    public static CommonResponse CODE_41007 = new CommonResponse(41007, "缺少子菜单数据");
    public static CommonResponse CODE_41008 = new CommonResponse(41008, "缺少oauth code");
    public static CommonResponse CODE_41009 = new CommonResponse(41009, "缺少openid");
    public static CommonResponse CODE_42001 = new CommonResponse(42001, "access_token超时");
    public static CommonResponse CODE_42002 = new CommonResponse(42002, "refresh_token超时");
    public static CommonResponse CODE_43001 = new CommonResponse(43001, "需要GET请求");
    public static CommonResponse CODE_43002 = new CommonResponse(43002, "需要POST请求");
    public static CommonResponse CODE_43003 = new CommonResponse(43003, "需要HTTPS请求");
    public static CommonResponse CODE_43004 = new CommonResponse(43004, "需要接收者关注");
    public static CommonResponse CODE_43005 = new CommonResponse(43005, "需要好友关系");
    public static CommonResponse CODE_44001 = new CommonResponse(44001, "多媒体文件为空");
    public static CommonResponse CODE_44002 = new CommonResponse(44002, "POST的数据包为空");
    public static CommonResponse CODE_44003 = new CommonResponse(44003, "图文消息内容为空");
    public static CommonResponse CODE_44004 = new CommonResponse(44004, "文本消息内容为空");
    public static CommonResponse CODE_45001 = new CommonResponse(45001, "多媒体文件大小超过限制");
    public static CommonResponse CODE_45002 = new CommonResponse(45002, "消息内容超过限制");
    public static CommonResponse CODE_45003 = new CommonResponse(45003, "标题字段超过限制");
    public static CommonResponse CODE_45004 = new CommonResponse(45004, "描述字段超过限制");
    public static CommonResponse CODE_45005 = new CommonResponse(45005, "链接字段超过限制");
    public static CommonResponse CODE_45006 = new CommonResponse(45006, "图片链接字段超过限制");
    public static CommonResponse CODE_45007 = new CommonResponse(45007, "语音播放时间超过限制");
    public static CommonResponse CODE_45008 = new CommonResponse(45008, "图文消息超过限制");
    public static CommonResponse CODE_45009 = new CommonResponse(45009, "接口调用超过限制");
    public static CommonResponse CODE_45010 = new CommonResponse(45010, "创建菜单个数超过限制");
    public static CommonResponse CODE_45012 = new CommonResponse(45012, "模板大小超过限制");
    public static CommonResponse CODE_45013 = new CommonResponse(45013, "模板参数超过限制");
    public static CommonResponse CODE_45014 = new CommonResponse(45014, "模板消息长度超过限制");
    public static CommonResponse CODE_45015 = new CommonResponse(45015, "回复时间超过限制");
    public static CommonResponse CODE_46001 = new CommonResponse(46001, "不存在媒体数据");
    public static CommonResponse CODE_46002 = new CommonResponse(46002, "不存在的菜单版本");
    public static CommonResponse CODE_46003 = new CommonResponse(46003, "不存在的菜单数据");
    public static CommonResponse CODE_46004 = new CommonResponse(46004, "不存在的用户");
    public static CommonResponse CODE_47001 = new CommonResponse(47001, "解析JSON/XML内容错误");
    public static CommonResponse CODE_48001 = new CommonResponse(48001, "api功能未授权");
    public static CommonResponse CODE_50001 = new CommonResponse(50001, "用户未授权该api");

    public CommonResponse() {
    }

    public CommonResponse(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    private Integer errcode;

    private String errmsg;

    @JsonIgnore
    public Integer getErrcode() {
        return errcode;
    }

    @JsonIgnore
    public String getErrmsg() {
        return errmsg;
    }

    @JsonIgnore
    public boolean failed() {
        return this.errcode != null && this.errcode != 0;
    }

    @Override
    public String toString() {
        return "ResultMessage{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
