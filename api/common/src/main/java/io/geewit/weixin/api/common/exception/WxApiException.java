package io.geewit.weixin.api.common.exception;


/**
 * @author geewit
 * @since 2022-01-07
 */
public class WxApiException extends IllegalArgumentException {
    public WxApiException(String message) {
        super(message);
    }
}
