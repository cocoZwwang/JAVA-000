package pers.cocoadel.cmq.comm.response;

import lombok.Data;

/**
 * 登录认证返回
 */
@Data
public class AuthResponseBody implements ResponseBody{
    private String token;
}
