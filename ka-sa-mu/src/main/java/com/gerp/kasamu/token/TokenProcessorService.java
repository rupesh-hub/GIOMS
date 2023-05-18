package com.gerp.kasamu.token;

public interface TokenProcessorService {
    Long getUserId();
    Long getEmpId();
    String getPisCode();
    String getOfficeCode();
    boolean isAdmin();

    Boolean getIsOfficeHead();
}
