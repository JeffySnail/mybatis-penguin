package com.coder.enhance.exception;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class MybatisSqlResourceException extends RuntimeException {

    public static final MybatisSqlResourceException newException(String message) {
        return new MybatisSqlResourceException(message);
    }
    public MybatisSqlResourceException() {
        super();
    }

    public MybatisSqlResourceException(String message) {
        super(message);
    }

    public MybatisSqlResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
