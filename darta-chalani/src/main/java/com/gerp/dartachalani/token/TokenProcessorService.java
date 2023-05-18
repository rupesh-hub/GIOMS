package com.gerp.dartachalani.token;

public interface TokenProcessorService {
    Long getUserId();
    Long getEmpId();
    String getPisCode();
    String getOfficeCode();
    String getRoles();
    Integer getDelegatedId();
    boolean isSuperAdmin();
    boolean isOrganizationAdmin();
    String getClientId();
}
