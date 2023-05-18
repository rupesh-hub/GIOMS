package com.gerp.shared.utils;

import com.gerp.shared.enums.Status;

public class DelegationUtils {
    public static boolean validToDelegation(Status status) {
        return status.equals(Status.C) || status.equals(Status.A) || status.equals(Status.R)|| status.equals(Status.F);
    }

    private DelegationUtils() {
        throw new IllegalStateException("Utility class");
        }
}
