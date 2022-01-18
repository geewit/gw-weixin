package io.geewit.weixin.api.mp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.weixin.api.common.model.Invoker;
import io.geewit.weixin.api.common.model.CommonRequest;
import io.geewit.weixin.api.common.model.CommonResponse;
import io.geewit.weixin.api.common.model.CommonInvoker;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * @author geewit
 * @since 2022-01-07
 */
public interface MP {

    /**
     * 二维码 接口定义
     */
    interface QrCode {

        /**
         * 生成带参数的二维码 接口定义
         */
        interface Create {
            CommonInvoker<Request, Response> INVOKER = CommonInvoker.<Request, Response>builder()
                    .name("生成带参数的二维码")
                    .uri("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token={accessToken}")
                    .method(HttpMethod.POST)
                    .request(Invoker.Request.<Request>builder().type(QrCode.Create.Request.class).build())
                    .response(Invoker.Response.<Response>builder().mediaType(MediaType.APPLICATION_JSON).build())
                    .build();

            /**
             * 生成带参数的二维码的请求参数
             */
            @Builder
            @Setter
            @Getter
            class Request extends CommonRequest {
                @JsonProperty("expire_seconds")
                private Integer expireSeconds;
                /**
                 * 二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
                 */
                @JsonProperty("action_name")
                private String actionName;
                /**
                 * 二维码详细信息
                 */
                @JsonProperty(value = "action_info")
                private ActionInfo actionInfo;
            }

            @Builder
            @Setter
            @Getter
            class ActionInfo {
                @JsonProperty("scene")
                private Scene scene;
            }

            @Builder
            @Setter
            @Getter
            class Scene {
                /**
                 * 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
                 */
                @JsonProperty("scene_id")
                private Integer sceneId;

                /**
                 * 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
                 */
                @JsonProperty("scene_str")
                private String sceneStr;
            }

            /**
             * 生成带参数的二维码的返回参数
             */
            @Setter
            @Getter
            class Response extends CommonResponse {
                /**
                 * 获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
                 */
                @JsonProperty(value = "ticket")
                private String ticket;

                @JsonProperty(value = "expire_seconds")
                private Integer expireSeconds;

                /**
                 * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
                 */
                @JsonProperty(value = "url")
                private String url;

                @Override
                public String toString() {
                    return "QrCodeCreate.Response {" +
                            "ticket=" + ticket +
                            ", expireSeconds=" + expireSeconds +
                            ", url=" + url +
                            '}';
                }
            }
        }



        /**
         * 通过ticket换取二维码 接口定义
         */
        interface Show {
            CommonInvoker<Request, Response> INVOKER = CommonInvoker.<Request, Response>builder()
                    .name("生成带参数的二维码")
                    .uri("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket={ticket}")
                    .method(HttpMethod.GET)
                    .request(Invoker.Request.<Request>builder().type(QrCode.Show.Request.class).build())
                    .response(Invoker.Response.<Response>builder().mediaType(MediaType.IMAGE_JPEG).build())
                    .build();

            @Builder
            @Setter
            @Getter
            class Request extends CommonRequest {
                /**
                 * 获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
                 */
                @JsonIgnore
                private String ticket;
            }


            @Setter
            @Getter
            class Response extends CommonResponse {
                /**
                 * 获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
                 */
                @JsonProperty(value = "ticket")
                private String ticket;

                @JsonProperty(value = "expire_seconds")
                private Integer expireSeconds;

                /**
                 * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
                 */
                @JsonProperty(value = "url")
                private String url;

                @Override
                public String toString() {
                    return "QrCodeCreate.Response {" +
                            "ticket=" + ticket +
                            ", expireSeconds=" + expireSeconds +
                            ", url=" + url +
                            '}';
                }
            }
        }
    }

}
