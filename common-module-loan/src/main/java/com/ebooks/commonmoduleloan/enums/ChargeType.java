package com.ebooks.commonmoduleloan.enums;

public enum ChargeType {
    PERCENTAGE,
    FLAT,
    GREATER; // max(percentage, flat)

    public static ChargeType fromString(String type) {
        return valueOf(type.toUpperCase());
    }
}