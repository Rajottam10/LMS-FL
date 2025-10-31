package com.ebooks.loandisbursementservice.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AdminFeeType {
    PERCENTAGE,
    FLAT_RATE,
    GREATER_AMONGST_FLAT_OR_PERCENTAGE
}