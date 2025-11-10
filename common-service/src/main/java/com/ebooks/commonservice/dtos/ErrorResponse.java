package com.ebooks.commonservice.dtos;

import lombok.Generated;

public class ErrorResponse {
    private String error;
    private String message;
    private Long timestamp = System.currentTimeMillis();

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    @Generated
    public String getError() {
        return this.error;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public Long getTimestamp() {
        return this.timestamp;
    }

    @Generated
    public void setError(final String error) {
        this.error = error;
    }

    @Generated
    public void setMessage(final String message) {
        this.message = message;
    }

    @Generated
    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    @Generated
    public ErrorResponse() {
    }
}
