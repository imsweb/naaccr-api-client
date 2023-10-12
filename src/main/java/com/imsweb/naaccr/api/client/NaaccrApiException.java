/*
 * Copyright (C) 2023 Information Management Services, Inc.
 */
package com.imsweb.naaccr.api.client;

import java.io.IOException;

public class NaaccrApiException extends IOException {

    private final Integer _code;

    public NaaccrApiException(Integer code, String message) {
        super(message);
        _code = code;
    }

    @SuppressWarnings("unused")
    public Integer getCode() {
        return _code;
    }
}
