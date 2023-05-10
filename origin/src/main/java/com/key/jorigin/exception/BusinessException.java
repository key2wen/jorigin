package com.key.jorigin.exception;

/**
 * @author zwh 2016/08/05
 *         业务异常(参数错误等情况),并非程序本身异常
 *         不打印异常堆栈信息,提升性能
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}