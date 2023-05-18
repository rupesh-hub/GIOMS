package com.gerp.templating.token;

public interface TokenProcessorService {
    Long getUserId();
    Long getEmpId();
    String getPisCode();
    String getOfficeCode();
    boolean isAdmin();
    Boolean getIsOfficeHead();

    String getClientId();
}
