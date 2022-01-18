package io.geewit.weixin.api.sns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.weixin.api.common.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 如果用户在微信客户端中访问第三方网页，公众号可以通过微信网页授权机制，来获取用户基本信息，进而实现业务逻辑。
 *
 * 关于网页授权回调域名的说明
 * 1、在微信公众号请求用户网页授权之前，开发者需要先到公众平台官网中的“开发 - 接口权限 - 网页服务 - 网页帐号 - 网页授权获取用户基本信息”的配置选项中，修改授权回调域名。请注意，这里填写的是域名（是一个字符串），而不是URL，因此请勿加 http:// 等协议头；
 * 2、授权回调域名配置规范为全域名，比如需要网页授权的域名为：www.qq.com，配置以后此域名下面的页面http://www.qq.com/music.html 、 http://www.qq.com/login.html 都可以进行OAuth2.0鉴权。但http://pay.qq.com 、 http://music.qq.com 、 http://qq.com 无法进行OAuth2.0鉴权
 * 3、如果公众号登录授权给了第三方开发者来进行管理，则不必做任何设置，由第三方代替公众号实现网页授权即可
 *
 * 关于网页授权的两种scope的区别说明
 * 1、以snsapi_base为scope发起的网页授权，是用来获取进入页面的用户的openid的，并且是静默授权并自动跳转到回调页的。用户感知的就是直接进入了回调页（往往是业务页面）
 * 2、以snsapi_userinfo为scope发起的网页授权，是用来获取用户的基本信息的。但这种授权需要用户手动同意，并且由于用户同意过，所以无须关注，就可在授权后获取该用户的基本信息。
 * 3、用户管理类接口中的“获取用户基本信息接口”，是在用户和公众号产生消息交互或关注后事件推送后，才能根据用户OpenID来获取用户基本信息。这个接口，包括其他微信接口，都是需要该用户（即openid）关注了公众号后，才能调用成功的。
 *
 * 关于网页授权access_token和普通access_token的区别
 * 1、微信网页授权是通过OAuth2.0机制实现的，在用户授权给公众号后，公众号可以获取到一个网页授权特有的接口调用凭证（网页授权access_token），通过网页授权access_token可以进行授权后接口调用，如获取用户基本信息；
 * 2、其他微信接口，需要通过基础支持中的“获取access_token”接口来获取到的普通access_token调用。
 *
 * 关于UnionID机制
 * 1、请注意，网页授权获取用户基本信息也遵循UnionID机制。即如果开发者有在多个公众号，或在公众号、移动应用之间统一用户帐号的需求，需要前往微信开放平台（open.weixin.qq.com）绑定公众号后，才可利用UnionID机制来满足上述需求。
 * 2、UnionID机制的作用说明：如果开发者拥有多个移动应用、网站应用和公众帐号，可通过获取用户基本信息中的unionid来区分用户的唯一性，因为同一用户，对同一个微信开放平台下的不同应用（移动应用、网站应用和公众帐号），unionid是相同的。
 *
 * 关于特殊场景下的静默授权
 * 1、上面已经提到，对于以snsapi_base为scope的网页授权，就静默授权的，用户无感知；
 * 2、对于已关注公众号的用户，如果用户从公众号的会话或者自定义菜单进入本公众号的网页授权页，即使是scope为snsapi_userinfo，也是静默授权，用户无感知。
 *
 * 具体而言，网页授权流程分为四步：
 * 1、引导用户进入授权页面同意授权，获取code
 * 2、通过code换取网页授权access_token（与基础支持中的access_token不同）
 * 3、如果需要，开发者可以刷新网页授权access_token，避免过期
 * 4、通过网页授权access_token和openid获取用户基本信息（支持UnionID机制）
 *
 * @author geewit
 * @since 2022-01-07
 */
public interface SNS {

    interface Login {
        /**
         * 第一步：用户同意授权，获取code
         * 在确保微信公众账号拥有授权作用域（scope参数）的权限的前提下（服务号获得高级接口后，默认拥有scope参数中的snsapi_base和snsapi_userinfo），引导关注者打开如下页面：
         * https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect 若提示“该链接无法访问”，请检查参数是否填写错误，是否拥有scope参数对应的授权作用域权限。
         * 尤其注意：由于授权操作安全等级较高，所以在发起授权请求时，微信会对授权链接做正则强匹配校验，如果链接的参数顺序不对，授权页面将无法正常访问
         *
         * 参考链接(请在微信客户端中打开此链接体验):
         * scope为snsapi_base
         * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx520c15f417810387&redirect_uri=https%3A%2F%2Fchong.qq.com%2Fphp%2Findex.php%3Fd%3D%26c%3DwxAdapter%26m%3DmobileDeal%26showwxpaytitle%3D1%26vb2ctag%3D4_2030_5_1194_60&response_type=code&scope=snsapi_base&state=123#wechat_redirect
         *
         * scope为snsapi_userinfo
         * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf0e81c3bee622d60&redirect_uri=http%3A%2F%2Fnba.bluewebgame.com%2Foauth_response.php&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
         * 尤其注意：跳转回调redirect_uri，应当使用https链接来确保授权code的安全性。
         */
        interface OAuth2Code {
            RedirectInvoker<Request, Response> INVOKER =
                    RedirectInvoker.<SNS.Login.OAuth2Code.Request, SNS.Login.OAuth2Code.Response>builder()
                    .name("用户同意授权,获取code")
                    .uri("https://open.weixin.qq.com/connect/oauth2/authorize?appid={appId}&redirect_uri={redirectUri}&response_type=code&scope={scope}&state={state}#wechat_redirect")
                    .method(HttpMethod.GET)
                    .request(Invoker.Request.<SNS.Login.OAuth2Code.Request>builder()
                            .mediaType(MediaType.TEXT_HTML)
                            .type(SNS.Login.OAuth2Code.Request.class)
                            .build())
                    .response(Invoker.Response.<SNS.Login.OAuth2Code.Response>builder()
                            .mediaType(MediaType.TEXT_HTML)
                            .expectCode(HttpStatus.FOUND.value())
                            .type(SNS.Login.OAuth2Code.Response.class)
                            .build())
                    .build();

            @Builder
            @Setter
            @Getter
            class Request implements IRequest {
                /**
                 * 公众号的唯一标识
                 */
                @JsonIgnore
                private String appId;
                /**
                 * 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
                 */
                @JsonIgnore
                private String redirectUri;

                /**
                 * 应用授权作用域
                 * snsapi_base (不弹出授权页面, 直接跳转, 只能获取用户openid),
                 * snsapi_userinfo (弹出授权页面, 可通过openid拿到昵称、性别、所在地. 并且, 即使在未关注的情况下, 只要用户授权, 也能获取其信息)
                 */
                @Builder.Default
                @JsonIgnore
                private RequestScope scope = RequestScope.snsapi_base;

                /**
                 * 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
                 */
                @JsonIgnore
                private String state;

                public void setRedirectUri(String redirectUri) {
                    try {
                        this.redirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        this.redirectUri = redirectUri;
                    }
                }

                @Override
                public String toString() {
                    return "OAuth2Code.Request{"
                            + "appId=" + appId
                            + ", redirectUri=" + redirectUri
                            + ", scope=" + scope
                            + ", state=" + state
                            + '}';
                }
            }

            /**
             * 应用授权作用域
             */
            enum RequestScope {
                /**
                 * 不弹出授权页面，直接跳转，只能获取用户openid
                 */
                snsapi_base,

                /**
                 * 弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息
                 */
                snsapi_userinfo
            }

            /**
             * code说明: code作为换取access_token的票据, 每次用户授权带上的code将不一样, code只能使用一次，5分钟未被使用自动过期.
             */
            @Setter
            @Getter
            class Response extends RedirectResponse {
                public Response() {
                    loadTimestamp = System.currentTimeMillis();
                }

                /**
                 * 获取access_token的时间, 用于和当前时间对比判断是否过期
                 */
                @JsonIgnore
                private Long loadTimestamp;

                /**
                 * 过期时间为 5 mins
                 * @return 是否过期
                 */
                public boolean expired() {
                    if(loadTimestamp == null) {//过期
                        return true;
                    } else {
                        long expiredTimestamp = loadTimestamp + 300000;
                        return System.currentTimeMillis() > expiredTimestamp;
                    }
                }

                @Override
                public String toString() {
                    return "OAuth2Code.Response {"
                            + "loadTimestamp=" + loadTimestamp
                            + '}';
                }
            }
        }

        /**
         * 第二步：通过code换取网页授权access_token
         * 首先请注意，这里通过code换取的是一个特殊的网页授权access_token,与基础支持中的access_token（该access_token用于调用其他接口）不同。公众号可通过下述接口来获取网页授权access_token。如果网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token的同时，也获取到了openid，snsapi_base式的网页授权流程即到此为止。
         * 尤其注意：由于公众号的secret和获取到的access_token安全级别都非常高，必须只保存在服务器，不允许传给客户端。后续刷新access_token、通过access_token获取用户信息等步骤，也必须从服务器发起。
         * 请求方法
         * 获取code后，请求以下链接获取access_token： https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
         */
        interface AccessToken {
            AccessTokenInvoker<Request, Response> INVOKER = AccessTokenInvoker.<Request, Response>builder()
                    .name("通过code换取网页授权access_token")
                    .uri("https://api.weixin.qq.com/sns/oauth2/access_token?appid={appId}&secret={secret}&code={code}&grant_type=authorization_code")
                    .method(HttpMethod.GET)
                    .request(Invoker.Request.<Request>builder()
                            .build())
                    .response(Invoker.Response.<SNS.Login.AccessToken.Response>builder()
                            .mediaType(MediaType.APPLICATION_JSON_UTF8)
                            .type(SNS.Login.AccessToken.Response.class)
                            .build())
                    .build();

            /**
             * appId: 公众号的唯一标识
             * secret: 公众号的appsecret
             */
            @SuperBuilder
            @Setter
            @Getter
            class Request extends AccessTokenRequest {
                /**
                 * 填写第一步获取的code参数
                 */
                @JsonIgnore
                private String code;

                @Override
                public String toString() {
                    return "AccessToken.Request {"
                            + "appId=" + appId
                            + ", secret=" + secret
                            + ", code=" + code
                            + '}';
                }
            }

            /**
             * access_token: 网页授权接口调用凭证,注意：此 access_token 与基础支持的 access_token 不同
             */
            @Setter
            @Getter
            class Response extends AccessTokenResponse {
                /**
                 * 用户刷新access_token
                 */
                @JsonProperty(value = "refresh_token")
                private String refreshToken;

                /**
                 * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
                 */
                @JsonProperty(value = "openid")
                private String openId;

                /**
                 * 用户授权的作用域，使用逗号（,）分隔
                 */
                @JsonProperty(value = "scope")
                private String scope;

                @Override
                public String toString() {
                    return "AccessToken.Response {" +
                            "accessToken=" + accessToken +
                            ", expiresIn=" + expiresIn +
                            ", refreshToken=" + refreshToken +
                            ", openId=" + openId +
                            ", scope=" + scope +
                            ", loadTimestamp=" + loadTimestamp +
                            '}';
                }
            }
        }

        /**
         * 第三步：刷新access_token（如果需要）
         * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
         *
         * 请求方法
         * 获取第二步的refresh_token后，请求以下链接获取access_token： https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
         */
        interface RefreshToken {
            AccessTokenInvoker<Request, Response> INVOKER = AccessTokenInvoker.<Request, Response>builder()
                    .name("刷新access_token")
                    .uri("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid={appId}&grant_type=refresh_token&refresh_token={refreshToken}")
                    .method(HttpMethod.GET)
                    .request(Invoker.Request.<Request>builder()
                            .type(Request.class)
                            .build())
                    .response(Invoker.Response.<Response>builder()
                            .mediaType(MediaType.APPLICATION_JSON_UTF8)
                            .type(Response.class)
                            .build())
                    .build();

            /**
             * appId: 公众号的唯一标识
             */
            @SuperBuilder
            @Setter
            @Getter
            class Request extends AccessTokenRequest {
                /**
                 * 填写通过access_token获取到的refresh_token参数
                 */
                @JsonIgnore
                private String refreshToken;

                @Override
                public String toString() {
                    return "RefreshToken.Request {"
                            + "appId=" + appId
                            + ", refreshToken=" + refreshToken
                            + '}';
                }
            }

            /**
             * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
             */
            @Setter
            @Getter
            class Response extends AccessTokenResponse {
                /**
                 * 用户刷新access_token
                 */
                @JsonProperty(value = "refresh_token")
                private String refreshToken;

                /**
                 * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
                 */
                @JsonProperty(value = "openid")
                private String openId;

                /**
                 * 用户授权的作用域，使用逗号（,）分隔
                 */
                @JsonProperty(value = "scope")
                private String scope;

                @Override
                public String toString() {
                    return "RefreshToken.Response {" +
                            "accessToken=" + accessToken +
                            ", expiresIn=" + expiresIn +
                            ", refreshToken=" + refreshToken +
                            ", openId=" + openId +
                            ", scope=" + scope +
                            ", loadTimestamp=" + loadTimestamp +
                            '}';
                }
            }
        }

        /**
         * 第四步：拉取用户信息(需scope为 snsapi_userinfo)
         * 如果网页授权作用域为snsapi_userinfo，则此时开发者可以通过access_token和openid拉取用户信息了。
         *
         * 请求方法
         * http：GET（请使用https协议） https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
         */
        interface UserInfo {
            CommonInvoker<Request, Response> INVOKER = CommonInvoker.<SNS.Login.UserInfo.Request, SNS.Login.UserInfo.Response>builder()
                    .name("拉取用户信息")
                    .uri("https://api.weixin.qq.com/sns/userinfo?access_token={accessToken}&openid={openId}&lang=zh_CN")
                    .method(HttpMethod.GET)
                    .request(Invoker.Request.<SNS.Login.UserInfo.Request>builder()
                            .type(SNS.Login.UserInfo.Request.class)
                            .build())
                    .response(Invoker.Response.<SNS.Login.UserInfo.Response>builder()
                            .mediaType(MediaType.APPLICATION_JSON_UTF8)
                            .type(SNS.Login.UserInfo.Response.class)
                            .build())
                    .build();

            /**
             * 拉取用户信息的请求参数
             */
            @Builder
            @Setter
            @Getter
            class Request extends CommonRequest {
                /**
                 * 用户的唯一标识
                 */
                @JsonIgnore
                private String openId;

                @Override
                public String toString() {
                    return "UserInfo.Request {"
                            + "accessToken=" + accessToken
                            + ", openId=" + openId
                            + '}';
                }
            }

            /**
             * 拉取用户信息的返回参数
             */
            @Setter
            @Getter
            class Response extends CommonResponse {
                /**
                 * 用户的唯一标识
                 */
                @JsonProperty(value = "openid")
                private String openId;

                /**
                 * 用户昵称
                 */
                @JsonProperty(value = "nickname")
                private String nickname;

                /**
                 * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
                 */
                @JsonProperty(value = "sex")
                private Integer sex;

                /**
                 * 用户个人资料填写的省份
                 */
                @JsonProperty(value = "province")
                private String province;

                /**
                 * 普通用户个人资料填写的城市
                 */
                @JsonProperty(value = "city")
                private String city;

                /**
                 * 国家，如中国为CN
                 */
                @JsonProperty(value = "country")
                private String country;

                /**
                 * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
                 */
                @JsonProperty(value = "headimgurl")
                private String headimgUrl;

                /**
                 * 用户特权信息，json 数组，如微信沃卡用户为(chinaunicom)
                 */
                @JsonProperty(value = "privilege")
                private List<String> privilege;

                /**
                 * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
                 */
                @JsonProperty(value = "unionid")
                private String unionId;

                @Override
                public String toString() {
                    return "UserInfo.Response {"
                            + "openId=" + openId
                            + ", nickname=" + nickname
                            + ", sex=" + sex
                            + ", province=" + province
                            + ", city=" + city
                            + ", country=" + country
                            + ", headimgUrl=" + headimgUrl
                            + ", privilege=" + privilege
                            + ", unionId=" + unionId
                            + '}';
                }
            }
        }
    }

}
