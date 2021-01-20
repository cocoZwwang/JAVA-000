package pers.cocoadel.cmq.server.comm.response;

import lombok.Data;

/**
 * 登录认证返回
 */
@Data
public class AuthResponseBody implements ResponseBody{

    /**
     * 200 成功
     */
    private Integer resultCode;

    private String message;
}
