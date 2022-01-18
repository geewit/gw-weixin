package io.geewit.weixin.api.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.weixin.api.common.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * @author geewit
 * @since 2022-01-07
 */
public interface COMMON {

    /**
     * access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。开发者需要进行妥善保存。
     * access_token的存储至少要保留512个字符空间。
     * access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取的access_token失效。
     *
     * 公众平台的API调用所需的access_token的使用及生成方式说明：
     * 1、建议公众号开发者使用中控服务器统一获取和刷新access_token，其他业务逻辑服务器所使用的access_token均来自于该中控服务器，不应该各自去刷新，否则容易造成冲突，导致access_token覆盖而影响业务；
     * 2、目前access_token的有效期通过返回的expire_in来传达，目前是7200秒之内的值。中控服务器需要根据这个有效时间提前去刷新新access_token。在刷新过程中，中控服务器可对外继续输出的老access_token，此时公众平台后台会保证在5分钟内，新老access_token都可用，这保证了第三方业务的平滑过渡；
     * 3、access_token的有效时间可能会在未来有调整，所以中控服务器不仅需要内部定时主动刷新，还需要提供被动刷新access_token的接口，这样便于业务服务器在API调用获知access_token已超时的情况下，可以触发access_token的刷新流程。
     * 4、对于可能存在风险的调用，在开发者进行获取 access_token调用时进入风险调用确认流程，需要用户管理员确认后才可以成功获取。具体流程为：
     *
     * 开发者通过某IP发起调用->平台返回错误码[89503]并同时下发模板消息给公众号管理员->公众号管理员确认该IP可以调用->开发者使用该IP再次发起调用->调用成功。
     * 如公众号管理员第一次拒绝该IP调用，用户在1个小时内将无法使用该IP再次发起调用，如公众号管理员多次拒绝该IP调用，该IP将可能长期无法发起调用。平台建议开发者在发起调用前主动与管理员沟通确认调用需求，或请求管理员开启IP白名单功能并将该IP加入IP白名单列表。
     * 公众号和小程序均可以使用AppID和AppSecret调用本接口来获取access_token。AppID和AppSecret可在“微信公众平台-开发-基本配置”页中获得（需要已经成为开发者，且帐号没有异常状态）。
     * 调用接口时，请登录“微信公众平台-开发-基本配置”提前将服务器IP地址添加到IP白名单中，点击查看设置方法，否则将无法调用成功。小程序无需配置IP白名单。
     *
     * 接口调用请求说明
     * https请求方式: GET https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     */
    interface AccessToken {
        AccessTokenInvoker<AccessTokenRequest, Response> INVOKER = AccessTokenInvoker.<AccessTokenRequest, Response>builder()
                .name("获取AccessToken")
                .uri("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={secret}")
                .method(HttpMethod.GET)
                .request(Invoker.Request.<AccessTokenRequest>builder()
                        .type(AccessTokenRequest.class)
                        .build())
                .response(Invoker.Response.<Response>builder()
                        .mediaType(MediaType.APPLICATION_JSON_UTF8)
                        .type(AccessToken.Response.class)
                        .build())
                .expiredSeconds(7200)
                .build();

        /**
         * 正常情况下，微信会返回下述JSON数据包给公众号：
         * {"access_token":"ACCESS_TOKEN","expires_in":7200}
         */
        @Setter
        @Getter
        class Response extends AccessTokenResponse {

            @Override
            public String toString() {
                return "AccessToken.Response {"
                        + "accessToken=" + accessToken
                        + ", expiresIn=" + expiresIn
                        + ", loadTimestamp=" + loadTimestamp
                        + '}';
            }
        }
    }

    /**
     * 获取用户基本信息(UnionID机制)
     */
    interface UserInfo {
        CommonInvoker<Request, Response> INVOKER = CommonInvoker.<Request, Response>builder()
                .name("获取用户基本信息")
                .uri("https://api.weixin.qq.com/cgi-bin/user/info?access_token={accessToken}&openid={openId}&lang=zh_CN")
                .method(HttpMethod.GET)
                .request(Invoker.Request.<Request>builder()
                        .type(UserInfo.Request.class)
                        .build())
                .response(Invoker.Response.<Response>builder()
                        .mediaType(MediaType.APPLICATION_JSON_UTF8)
                        .type(UserInfo.Response.class)
                        .build())
                .tokenInvoker(AccessToken.INVOKER)
                .build();

        @Builder
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
                return "UserInfo.Request {"
                        + ", accessToken=" + super.accessToken
                        + ", openId=" + openId
                        + '}';
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
                return "UserInfo.Response {"
                        + "subscribe=" + subscribe
                        + ", openId=" + openId
                        + ", language=" + language
                        + ", subscribeTime=" + subscribeTime
                        + ", unionId=" + unionId
                        + ", remark=" + remark
                        + ", groupId=" + groupId
                        + ", tagidList=" + tagidList
                        + ", subscribeScene=" + subscribeScene
                        + ", qrScene=" + qrScene
                        +", qrSceneStr=" + qrSceneStr
                        + '}';
            }
        }
    }

    /**
     * 获取用户列表
     */
    interface UserList {
        CommonInvoker<Request, Response> INVOKER = CommonInvoker.<Request, Response>builder()
                .name("获取用户基本信息")
                .uri("https://api.weixin.qq.com/cgi-bin/user/get?access_token={accessToken}&next_openid={openId}")
                .method(HttpMethod.GET)
                .request(Invoker.Request.<Request>builder()
                        .type(UserList.Request.class)
                        .build())
                .response(Invoker.Response.<Response>builder().mediaType(MediaType.APPLICATION_JSON_UTF8).type(UserList.Response.class).build())
                .tokenInvoker(AccessToken.INVOKER)
                .build();

        @Builder
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
                return "UserList.Request {"
                        + "accessToken=" + super.accessToken
                        + "openId=" + openId
                        + '}';
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
