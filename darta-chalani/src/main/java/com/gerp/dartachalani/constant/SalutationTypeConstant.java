package com.gerp.dartachalani.constant;

public enum SalutationTypeConstant {
    OFFICE, EXTERNAL_OFFICE, INDIVIDUAL;

    public static SalutationTypeConstant getEnum(Integer type) {
        switch (type) {
            case 0:
                return SalutationTypeConstant.OFFICE;
            case 1:
                return SalutationTypeConstant.EXTERNAL_OFFICE;
            case 2:
                return SalutationTypeConstant.INDIVIDUAL;
            default:
                return null;
        }
    }
    }
