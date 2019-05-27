package com.ivay.ivay_manage.advice;

import com.ivay.ivay_manage.dto.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.Map;

/**
 * springmvc异常处理
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response badRequestException(IllegalArgumentException exception) {
        log.error("BAD_REQUEST: {}", exception);
        return new Response(HttpStatus.BAD_REQUEST.value() + "", exception.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response badRequestException(AccessDeniedException exception) {
        log.error("FORBIDDEN: {}", exception);
        return new Response(HttpStatus.FORBIDDEN.value() + "", exception.getMessage());
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            UnsatisfiedServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response badRequestException(Exception exception) {
        log.error("BAD_REQUEST: {}", exception);
        return new Response(HttpStatus.BAD_REQUEST.value() + "", exception.getMessage());
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response badRequestException(BusinessException exception) {
        log.error("业务报错: {}", exception);
        if (StringUtils.isEmpty(exception.getCode())) {
            exception.setCode(HttpStatus.FORBIDDEN.value() + "");
        }
        return new Response(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("参数校验报错: {}", exception);
        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("");
        for (int i = 0; i < bindingResult.getFieldErrors().size(); i++) {
            if (i > 0) {
                errorMessage.append(", ");
            }
            FieldError fieldError = bindingResult.getFieldErrors().get(i);
            errorMessage.append(fieldError.getField());
            errorMessage.append(":");
            errorMessage.append(fieldError.getDefaultMessage());
        }
        return new Response(HttpStatus.METHOD_NOT_ALLOWED.value() + "", errorMessage.toString());
    }

    @ExceptionHandler({
            ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.OK)
    public Response handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("参数校验报错: {}", exception);
        Iterator it = exception.getConstraintViolations().iterator();
        StringBuilder error = new StringBuilder();
        while (it.hasNext()) {
            ConstraintViolation s = (ConstraintViolation) it.next();
            try {
                Map map = s.getConstraintDescriptor().getAttributes();
                error.append(new StringBuilder(s.getMessageTemplate()));
            } catch (Exception ex) {
                error.append(s.getMessageTemplate());
            }
        }
        return new Response(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()), error.toString());
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Throwable throwable) {
        log.error("系统内部错误: {}", throwable);
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value() + "", throwable.getMessage());
    }
}
