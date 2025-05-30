package com.hubis.acs.common.handler.exception;

import lombok.Getter;

@Getter
public class CustomException extends Exception {

    private static final long serialVersionUID = 1L;

    private String ErrorCode;
    private Object[] ErrorArguments;

    public CustomException(String errorCode)
    {
        ErrorCode = errorCode;
    }

    public CustomException(String errorCode, Object... args)
    {
        ErrorCode = errorCode;
        ErrorArguments = args;
    }
}
