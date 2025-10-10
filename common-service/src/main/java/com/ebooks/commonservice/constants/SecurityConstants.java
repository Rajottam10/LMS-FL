package com.ebooks.commonservice.constants;

public class SecurityConstants {
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION = 86400000; // 24 hours

    // Role constants
    public static final String ROLE_GOD = "GOD";
    public static final String ROLE_BANK_ADMIN = "BANK_ADMIN";
    public static final String ROLE_BANK_USER = "BANK_USER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_LOAN_CONFIGURATION = "LOAN_CONFIGURATION";

    // Status constants
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_DELETED = "DELETED";
    public static final String STATUS_BLOCKED = "BLOCKED";

    // Permission constants
    public static final String PERMISSION_CREATE_BANK_USER = "CREATE_BANK_USER";
    public static final String PERMISSION_VIEW_BANK_USER = "VIEW_BANK_USER";
    public static final String PERMISSION_BLOCK_USER = "BLOCK_USER";
    public static final String PERMISSION_UNBLOCK_USER = "UNBLOCK_USER";
    public static final String PERMISSION_DELETE_USER = "DELETE_USER";
}