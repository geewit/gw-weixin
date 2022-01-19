package io.geewit.weixin.api.event.mp.model;

import com.google.common.hash.Hashing;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 验证消息的确来自微信服务器的签名
 * @author geewit
 * @since 2022-01-19
 */
@Builder
@Setter
@Getter
public class Signature implements Serializable {
    public static Signature of(String signature, String token, long timestamp, String nonce) {
        Signature mpSignature = Signature.builder()
                .token(token)
                .timestamp(timestamp)
                .nonce(nonce)
                .build();
        mpSignature.isAccessable = StringUtils.equalsIgnoreCase(mpSignature.toSignature(), signature);
        return mpSignature;
    }

    public static Signature of(String signature, String token, long timestamp, String nonce, String echostr) {
        Signature mpSignature = Signature.builder()
                .token(token)
                .timestamp(timestamp)
                .nonce(nonce)
                .echostr(echostr)
                .build();
        mpSignature.isAccessable = StringUtils.equalsIgnoreCase(mpSignature.toSignature(), signature);
        return mpSignature;
    }

    private String token;
    private Long timestamp;
    private String nonce;
    private String echostr;
    private boolean isAccessable;

    @SuppressWarnings("deprecation")
    public String toSignature() {
        String contactStr = Stream.of(this.token, this.nonce, String.valueOf(this.timestamp)).sorted().collect(Collectors.joining());
        return Hashing.sha1().hashString(contactStr, StandardCharsets.UTF_8).toString();
    }
}
