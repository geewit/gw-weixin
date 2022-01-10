package io.geewit.weixin.api.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.weixin.api.common.model.API;
import io.geewit.weixin.api.common.model.CommonRequest;
import io.geewit.weixin.api.common.model.CommonResponse;
import io.geewit.weixin.api.common.model.WeixinAPI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author geewit
 * @since 2022-01-07
 */
public interface APIs {


    /**
     * 获取 AccessToken 接口定义
     */
    interface AccessToken {
        WeixinAPI<Request, Response> INVOKER = WeixinAPI.<Request, Response>builder()
                .name("获取AccessToken")
                .uri("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={secret}")
                .method(HttpMethod.GET)
                .request(API.Request.<Request>builder().type(AccessToken.Request.class).build())
                .response(API.Response.<Response>builder().mediaType(MediaType.APPLICATION_JSON_UTF8).type(AccessToken.Response.class).build())
                .build();

        @Setter
        @Getter
        class Request extends CommonRequest {
            /**
             * 第三方用户唯一凭证
             */
            @JsonIgnore
            private String appId;
            /**
             * 第三方用户唯一凭证密钥,即appsecret
             */
            @JsonIgnore
            private String secret;

            @Override
            public String toString() {
                return "FetchAccessToken.Request {" +
                        "appId=" + appId +
                        ", secret=" + secret +
                        '}';
            }
        }

        @Setter
        @Getter
        class Response extends CommonResponse {
            public Response() {
                loadTimestamp = System.currentTimeMillis();
            }

            /**
             * 获取到的凭证
             */
            @JsonProperty(value = "access_token")
            private String accessToken;

            /**
             * 凭证有效时间，单位：秒
             */
            @JsonProperty(value = "expires_in")
            private Integer expiresIn;

            /**
             * 获取access_token的时间, 用于和当前时间对比判断是否过期
             */
            @JsonIgnore
            private Long loadTimestamp;

            public boolean expired() {
                if(loadTimestamp == null) {//过期
                    return true;
                } else {
                    long expiredTimestamp = loadTimestamp + expiresIn * 1000;
                    return System.currentTimeMillis() > expiredTimestamp;
                }
            }

            @Override
            public String toString() {
                return "AccessToken.Response {" +
                        "accessToken=" + accessToken +
                        ", expiresIn=" + expiresIn +
                        ", loadTimestamp=" + loadTimestamp +
                        '}';
            }
        }
    }

    /**
     * 获取用户基本信息(UnionID机制)
     */
    interface UserInfo {
        WeixinAPI<Request, Response> INVOKER = WeixinAPI.<Request, Response>builder()
                .name("获取用户基本信息")
                .uri("https://api.weixin.qq.com/cgi-bin/user/info?access_token={accessToken}&openid={openId}&lang=zh_CN")
                .method(HttpMethod.GET)
                .request(API.Request.<Request>builder().type(UserInfo.Request.class).withToken(true).build())
                .response(API.Response.<Response>builder().mediaType(MediaType.APPLICATION_JSON_UTF8).type(UserInfo.Response.class).build())
                .build();

        @Setter
        @Getter
        class Request extends CommonRequest {
            /**
             * 普通用户的标识,对当前公众号唯一
             */
            @JsonIgnore
            private String openId;

            @Override
            public String toString() {
                return "UserInfo.Request {" +
                        "accessToken=" + accessToken +
                        ", openId=" + openId +
                        '}';
            }
        }

        @Setter
        @Getter
        class Response extends CommonResponse {
            /**
             * 用户是否订阅该公众号标识,值为0时,代表此用户没有关注该公众号,拉取不到其余信息
             */
            @JsonProperty(value = "subscribe")
            private Integer subscribe;

            /**
             * 用户的标识,对当前公众号唯一
             */
            @JsonProperty(value = "openid")
            private String openId;

            /**
             * 用户的语言,简体中文为zh_CN
             */
            @JsonProperty(value = "language")
            private String language;

            /**
             * 用户关注时间,为时间戳。如果用户曾多次关注,则取最后关注时间
             */
            @JsonProperty(value = "subscribe_time")
            private Integer subscribeTime;

            /**
             * 只有在用户将公众号绑定到微信开放平台帐号后,才会出现该字段。
             */
            @JsonProperty(value = "unionid")
            private String unionId;

            /**
             * 公众号运营者对粉丝的备注,公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
             */
            @JsonProperty(value = "remark")
            private String remark;

            /**
             * 用户所在的分组ID（兼容旧的用户分组接口）
             */
            @JsonProperty(value = "groupid")
            private Integer groupId;

            /**
             * 用户被打上的标签ID列表
             */
            @JsonProperty(value = "tagid_list")
            private List<Integer> tagidList;

            /**
             * 返回用户关注的渠道来源,
             * ADD_SCENE_SEARCH 公众号搜索
             * ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移
             * ADD_SCENE_PROFILE_CARD 名片分享
             * ADD_SCENE_QR_CODE 扫描二维码
             * ADD_SCENE_PROFILE_LINK 图文页内名称点击
             * ADD_SCENE_PROFILE_ITEM 图文页右上角菜单
             * ADD_SCENE_PAID 支付后关注
             * ADD_SCENE_WECHAT_ADVERTISEMENT 微信广告
             * ADD_SCENE_REPRINT 他人转载
             * ADD_SCENE_LIVESTREAM 视频号直播
             * ADD_SCENE_CHANNELS 视频号
             * ADD_SCENE_OTHERS 其他
             */
            @JsonProperty(value = "subscribe_scene")
            private String subscribeScene;

            /**
             * 二维码扫码场景（开发者自定义）
             */
            @JsonProperty(value = "qr_scene")
            private Integer qrScene;

            /**
             * 二维码扫码场景描述（开发者自定义）
             */
            @JsonProperty(value = "qr_scene_str")
            private String qrSceneStr;

            @Override
            public String toString() {
                return new StringJoiner(", ", "UserInfo." + Response.class.getSimpleName() + "[", "]")
                        .add("subscribe=" + subscribe)
                        .add("openId=" + openId)
                        .add("language=" + language)
                        .add("subscribeTime=" + subscribeTime)
                        .add("unionId=" + unionId)
                        .add("remark=" + remark)
                        .add("groupId=" + groupId)
                        .add("tagidList=" + tagidList)
                        .add("subscribeScene=" + subscribeScene)
                        .add("qrScene=" + qrScene)
                        .add("qrSceneStr=" + qrSceneStr)
                        .toString();
            }
        }
    }


    /**
     * 获取用户列表
     */
    interface UserList {
        WeixinAPI<Request, Response> INVOKER = WeixinAPI.<Request, Response>builder()
                .name("获取用户基本信息")
                .uri("https://api.weixin.qq.com/cgi-bin/user/get?access_token={accessToken}&next_openid={openId}")
                .method(HttpMethod.GET)
                .request(API.Request.<Request>builder().type(UserList.Request.class).withToken(true).build())
                .response(API.Response.<Response>builder().mediaType(MediaType.APPLICATION_JSON_UTF8).type(UserList.Response.class).build())
                .build();

        @Setter
        @Getter
        class Request extends CommonRequest {
            /**
             * 第一个拉取的OPENID，不填默认从头开始拉取
             */
            @JsonIgnore
            private String openId;

            @Override
            public String toString() {
                return "UserList.Request {" +
                        "accessToken=" + accessToken +
                        ", openId=" + openId +
                        '}';
            }
        }

        @Setter
        @Getter
        class Response extends CommonResponse {
            /**
             * 关注该公众账号的总用户数
             */
            @JsonProperty(value = "total")
            private Integer total;

            /**
             * 拉取的OPENID个数, 最大值为10000
             */
            @JsonProperty(value = "count")
            private Integer count;

            /**
             * 列表数据, OPENID的列表
             */
            @JsonProperty(value = "data")
            private ResponseData data;

            /**
             * 拉取列表的最后一个用户的 openid
             */
            @JsonProperty(value = "next_openid")
            private String nextOpenid;

            @Override
            public String toString() {
                return "UserList.Response {"
                        + "total=" + total
                        + ", count=" + count
                        + ", data=" + data
                        + ", nextOpenid=" + nextOpenid
                        + '}';
            }
        }

        @Setter
        @Getter
        class ResponseData {
            /**
             * 列表数据, OPENID的列表
             */
            @JsonProperty(value = "openid")
            private List<String> openid;

            @Override
            public String toString() {
                return "Data{" + "openIds=" + openid + '}';
            }
        }
    }
}
