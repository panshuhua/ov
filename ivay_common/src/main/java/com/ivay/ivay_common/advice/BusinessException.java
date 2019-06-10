package com.ivay.ivay_common.advice;

/**
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {
    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    private String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * @param message 需要显示的异常信息
     * @param cause   为Throwable或其子类的实例
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
