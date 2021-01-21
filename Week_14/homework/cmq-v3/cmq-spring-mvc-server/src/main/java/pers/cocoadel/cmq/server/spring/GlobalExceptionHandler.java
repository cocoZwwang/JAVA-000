package pers.cocoadel.cmq.server.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.cocoadel.cmq.comm.exception.CmqOperationException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RspErr handlerError(Exception e) {
        if (e instanceof CmqOperationException) {
            setHttpStatus(((CmqOperationException) e).getCode());
            return new RspErr(e.getClass().getName(), e.getMessage());
        }
        setHttpStatus((HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return new RspErr("internalServerError", "服务器内部错误: " + e.getMessage());
    }

    public static void setHttpStatus(int code) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletResponse response = servletRequestAttributes.getResponse();
            if (response != null) {
                response.setStatus(code);
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class RspErr {
        private String error;

        private String message;
    }
}
