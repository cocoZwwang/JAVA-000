package pers.cocoadel.cmq.comm.request;

import lombok.Data;

/**
 * 账号认证
 */
@Data
public class AuthRequestBody extends RequestBody{
    private String name;

    private String password;
}
