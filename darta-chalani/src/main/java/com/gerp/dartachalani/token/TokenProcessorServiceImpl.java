package com.gerp.dartachalani.token;

import com.gerp.shared.utils.StringConstants;
import org.keycloak.adapters.jaas.AbstractKeycloakLoginModule;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
public class
TokenProcessorServiceImpl implements TokenProcessorService {


    @Override
    public Long getUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("userId") != null ? Long.valueOf(data.get("userId").toString()) : null;
        } else
            return null;
    }

    @Override
    public Long getEmpId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("empId") != null ? Long.valueOf(data.get("empId").toString()) : null;
        } else
            return null;
    }

    @Override
    public String getPisCode() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("pisCode") != null ? data.get("pisCode").toString() : null;
        } else
            return null;
    }

    @Override
    public String getOfficeCode() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("officeCode") != null ? data.get("officeCode").toString() : null;
        } else
            return null;
    }

    @Override
    public String getRoles() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("roles") != null ? data.get("roles").toString() : null;
        } else
            return null;
    }

    @Override
    public Integer getDelegatedId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("delegatedId") != null ? (Integer) data.get("delegatedId") : null;
        } else
            return null;
    }

    @Override
    public boolean isSuperAdmin() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            if (data.get("roles") != null && !data.get("roles").toString().trim().equals(""))
                return Arrays.asList(data.get("roles").toString().split(",")).stream().anyMatch(x -> x.equals(StringConstants.ADMIN_ROLE));
            else
                return false;
        } else
            return false;
    }

    @Override
    public boolean isOrganizationAdmin() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            if (data.get("roles") != null && !data.get("roles").toString().trim().equals(""))
                return Arrays.asList(data.get("roles").toString().split(",")).stream().anyMatch(x -> x.equals(StringConstants.ORGANISATION_ADMIN));
            else
                return false;
        } else
            return false;
    }

    @Override
    public String getClientId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();

            return account.getKeycloakSecurityContext().getToken().getIssuedFor();
        } else
            return null;
    }
}
