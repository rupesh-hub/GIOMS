package com.gerp.usermgmt.config.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OrganisationType;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrganizationIdGenerator implements ValueGenerator<OrganisationType> {

    @Override
    public OrganisationType generateValue(Session session, Object o) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            OrganisationType organisationType = getOrganisationType(o);
            if(organisationType != null){
                return organisationType;
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            //todo: for super admin there is no organization type detail find the solution
            Long organizationTypeId = data.get("organizationTypeId")!=null? Long.valueOf(data.get("organizationTypeId").toString()):null;
            return getOrganisationType(organizationTypeId);
        }else
            return null;    }

    private OrganisationType getOrganisationType(Long id) {
        OrganisationType o = new OrganisationType();
        o.setId(id);
        return o;
    }

    // refactor this with better way
    private OrganisationType getOrganisationType(Object object) {
        // In case of adding the functional designation
        if(object instanceof FunctionalDesignation){
            FunctionalDesignation functionalDesignation = (FunctionalDesignation) object;
            if(functionalDesignation.getOrganisationType() ==null || functionalDesignation.getOrganisationType().getId() ==null) return null;
            OrganisationType organisationType = new OrganisationType();
            organisationType.setId(functionalDesignation.getOrganisationType().getId());
            return organisationType;
        }
       Map<String, Object> data = (Map<String, Object>) new ObjectMapper().convertValue(object, Map.class).get("organisationType");
       if(data != null && data.get("id") != null) {
           Long id = (Long) data.get("id");
           return  getOrganisationType(id);
      }

       return null;
    }
}
