package io.geewit.weixin.event.mp.common.model;

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


    private String token;
    private String timestamp;
    private String nonce;

    @SuppressWarnings("deprecation")
    public String toSignature() {
        String contactStr = Stream.of(this.token, this.nonce, this.timestamp).sorted().collect(Collectors.joining());
        return Hashing.sha1().hashString(contactStr, StandardCharsets.UTF_8).toString();
    }

    public boolean isAccessable(String signature) {
        return StringUtils.equalsIgnoreCase(this.toSignature(), signature);
    }
}
