package com.gerp.usermgmt.config.generator;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.usermgmt.model.office.OrganisationType;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.hibernate.SessionException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class PrefixGenerationUtils {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private CustomMessageSource customMessageSource;


    /**
     * @param organisationType master type of the organization
     * @param code             main code to add with prefix
     * @param session          session for adding connection to db
     * @return concat data of code with prefix of organisation type
     */
    public String getPrefix(OrganisationType organisationType, String code, SharedSessionContractImplementor session) {
        Long organisationTypeId = null;
        if (organisationType != null) {
            organisationTypeId = organisationType.getId();
        }
        if (organisationTypeId == null) {
            organisationTypeId = this.tokenProcessorService.getOrganisationTypeId();
        }
        if (organisationTypeId == null) {
            throw new ServiceValidationException(customMessageSource.get("invalid.create.request"));
        }
        if (code == null) {
            throw new ServiceValidationException(customMessageSource.get("invalid.code"));
        }
        Connection connection = session.connection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select ot.prefix as prefix from organisation_type ot where ot.id = " + organisationTypeId + ";");
            if (rs.next()) {
                return rs.getString(1).trim().concat("_").concat(code);
            } else {
                throw new ServiceValidationException("error.cant.update");
            }
        } catch (Exception ex) {
            throw new SessionException(customMessageSource.get("invalid.create.request"));
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Issue while closing connection in OfficeCodeGenerator Class");
                e.printStackTrace();
            }
        }
    }

    public String getPrefix(OrganisationType organisationType, SharedSessionContractImplementor session) {
        Long organisationTypeId = null;
        if (organisationType != null) {
            organisationTypeId = organisationType.getId();
        }
        if (organisationTypeId == null) {
            organisationTypeId = this.tokenProcessorService.getOrganisationTypeId();
        }
        if (organisationTypeId == null) {
            throw new ServiceValidationException(customMessageSource.get("invalid.create.request"));
        }
        Connection connection = session.connection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select ot.prefix as prefix from organisation_type ot where ot.id = " + organisationTypeId + ";");
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new ServiceValidationException("error.cant.update");
            }
        } catch (Exception ex) {
            throw new SessionException(customMessageSource.get("invalid.create.request"));
        }
    }

    public String getOfficePrefix(Long organisationTypeId, SharedSessionContractImplementor session, String officeCode) {


        if (organisationTypeId == null) {
            organisationTypeId = this.tokenProcessorService.getOrganisationTypeId();
        }
        Connection connection = session.connection();
        if (organisationTypeId == null) {
            try (Statement statement = connection.createStatement()) {

                ResultSet rs = statement.executeQuery("select o.organisation_type_id from office o where o.code =  '"+officeCode+"' ;");
                if (rs.next()) {
                    organisationTypeId = rs.getLong(1);
                } else {
                    throw new ServiceValidationException("error.cant.update");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new SessionException(customMessageSource.get("invalid.create.request"));
            }
            //throw new ServiceValidationException(customMessageSource.get("invalid.create.request"));
        }


        try (Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("select ot.prefix as prefix from organisation_type ot where ot.id = " + organisationTypeId + ";");
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new ServiceValidationException("error.cant.update");
            }
        } catch (Exception ex) {
            throw new SessionException(customMessageSource.get("invalid.create.request"));
        }
    }
}
