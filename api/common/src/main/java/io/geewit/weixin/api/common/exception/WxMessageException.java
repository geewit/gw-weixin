package io.geewit.weixin.api.common.exception;


/**
 * @author geewit
 * @since 2022-01-07
 */
public class WxMessageException extends IllegalArgumentException {
    public WxMessageException(String message) {
        super(message);
    }
}