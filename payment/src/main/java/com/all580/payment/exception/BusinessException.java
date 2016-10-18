package com.all580.payment.exception;

/**
 * 业务异常类，抛出异常时，可以包装业务数据
 *
 * @author Created by panyi on 2016/10/9.
 */
public class BusinessException extends RuntimeException {
    private Object data = null;

    public BusinessException(String msg, Object data) {
        super(msg);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
