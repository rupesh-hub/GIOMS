package com.gerp.usermgmt.token;

public interface TokenProcessorService {
    Long getUserId();
    Long getEmpId();
    String getPisCode();
    Long getOrganisationTypeId();
    String getOfficeCode();
    boolean isAdmin();
    boolean isOrganisationAdmin();
    boolean isOfficeAdmin();

    Boolean getIsOfficeHead();
    boolean isDelegated();
    String getPreferredUsername();
    boolean isUser();
    Integer getDelegatedId();

    String getClientId();
}
