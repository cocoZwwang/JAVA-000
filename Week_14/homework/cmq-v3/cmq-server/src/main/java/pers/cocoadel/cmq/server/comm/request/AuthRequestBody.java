package pers.cocoadel.cmq.server.comm.request;

import lombok.Data;

/**
 * 账号认证
 */
@Data
public class AuthRequestBody implements RequestBody{
    private String name;

    private String password;
}
