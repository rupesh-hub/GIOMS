package com.gerp.attendance.token;

public interface TokenProcessorService {
    Long getUserId();
    Long getEmpId();
    String getPisCode();
    String getOfficeCode();
    boolean isAdmin();
    boolean isGeneralUser();
    Boolean getIsOfficeHead();
    Integer getDelegatedId();
    Boolean isOfficeAdministrator();
    String getRoles();
}
