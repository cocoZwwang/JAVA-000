package pers.cocoadel.cmq.server.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.cocoadel.cmq.comm.exception.CmqOperationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<RspErr> handlerError(Exception e) {
        if (e instanceof CmqOperationException) {
            RspErr rspErr =  new RspErr(e.getClass().getName(), e.getMessage());
            return ResponseEntity
                    .status(((CmqOperationException) e).getCode())
                    .body(rspErr);
        }
        RspErr rspErr = new RspErr("internalServerError", "服务器内部错误: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(rspErr);
    }

    @Data
    @AllArgsConstructor
    public static class RspErr {
        private String error;

        private String message;
    }
}
