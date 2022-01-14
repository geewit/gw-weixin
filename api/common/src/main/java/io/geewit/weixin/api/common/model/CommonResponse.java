package io.geewit.weixin.api.common.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;

/**
 * 通用的Api请求响应定义对象
 * @author geewit
 * @since 2022-01-07
 */
@Setter
public class CommonResponse implements IResponse {
    private static final long serialVersionUID = 1L;

    public CommonResponse() {
    }

    public CommonResponse(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    protected Integer errcode;

    protected String errmsg;

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
        return "CommonResponse{"
                + "errcode=" + errcode
                + ", errmsg=" + errmsg +
                '}';
    }
}
