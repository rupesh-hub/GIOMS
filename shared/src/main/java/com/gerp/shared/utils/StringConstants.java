package com.gerp.shared.utils;

public class StringConstants {
    public static final String NEPALI_PATTERN = "[\\u0900-\\u097F ].+";
    public static final String ALPHABET = "^[\\w\\s]+$";
    public static final String NUMERIC = "^\\d+$";
    public static final String NUMERIC_NULL = "^(\\d+)?$";
    public static final String PHONENUMBER = "^\\d || - || + +$ + ";
    public static final String ALPHA_NUMERIC = "^[\\w\\s\\d]+$";
    public static final String NEPALI_PATTERN_NOT_REQUIRED = "[\\u0900-\\u097F ]+||^$";
    public static final String NEPALI_PATTERN_NULL = "([\\u0900-\\u097F ]+)?";
    public static final String YEAR_PATTERN = "yyyy";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String ENGLISH_PATTERN = "^[a-zA-Z ]*$";
    public static final String ALPHA_PATTERN = "^[\\w\\s\\d\\'-]+$";
    public static final int DEFAULT_MAX_SIZE = 50;
    public static final int DEFAULT_MAX_SIZE_10 = 10;
    public static final int DEFAULT_MAX_SIZE2 = 100;
    public static final int DEFAULT_SHORT_SIZE = 25;
    public static final int DEFAULT_DECS_SIZE = 250;
    public static final int DEFAULT_CODE_SIZE = 15;
    public static final int MINISTRY_CODE_SIZE = 3;
    public static final int DEFAULT_MIN_SIZE = 6;
    public static final int DEFAULT_MIN_SIZE_USER = 3;
    public static final int DEFAULT_MIN_SIZE_4 = 4;
    public static final int PHONE_MAX_SIZE = 14;
    public static final int PHONE_MIN_SIZE = 10;
    public static final int WARD_MAX_SIZE = 2;
    public static final int DEFAULT_MAX_SIZE_20 = 20;
    public static final int DEFAULT_MAX_SIZE_30 = 30;
    public static final int DEFAULT_MAX_SIZE_2 = 2;
    public static final int DEFAULT_MAX_SIZE_200 = 200;
    public static final int DEFAULT_MAX_SIZE_6 = 6;
    public static final int DEFAULT_MAX_SIZE_8 = 8;
    public static final int DEFAULT_MAX_LIMIT = 254;
    public static final int GRADE_MAX_SIZE = 1;

    public static final String ADMIN_ROLE = "SUPER_ADMIN";
    public static final String ORGANISATION_ADMIN = "ORGANIZATIONADMIN";
    public static final String OFFICE_HEAD_ROLE = "OFFICE_HEAD";
    public static final String SECTION_HEAD_ROLE = "SECTION_HEAD";
    public static final String OFFICE_ADMINISTRATOR_ROLE = "OFFICE_ADMINISTRATOR";
    public static final String GENERAL_USER_ROLE = "GENERAL_USER";
    public static final String APPROVER_ROLE = "APPROVER";
    public static final String REVIEWER_ROLE = "REVIEWER";
    public static final String DASHBOARD_DAILY_LOG_VIEW_ROLE = "DASHBOARD_DAILY_LOG_VIEW";

    public static final String SERVICE = "service";
    public static final String GROUP  = "group";
    public static final String SUBGROUP  = "subgroup";

    public static final String READ_PRIVILEGE = "READ";

    public static final String PDF = "pdf";
    public static final String PNG = "png";
    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";

    public static final int DEFAULT_MAX_SIZE_100 = 100;
    public static final int DEFAULT_MAX_SIZE_50 = 50;
    public static final int DEFAULT_MAX_SIZE_15 = 15;
}
