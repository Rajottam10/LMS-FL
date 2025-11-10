package com.ebooks.bankservice.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat(
        shape = Shape.STRING
)
public enum AdminFeeType {
    PERCENTAGE,
    FLAT_RATE,
    GREATER_AMONGST_FLAT_OR_PERCENTAGE;
}