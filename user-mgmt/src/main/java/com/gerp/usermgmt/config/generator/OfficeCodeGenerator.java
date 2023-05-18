package com.gerp.usermgmt.config.generator;

import com.gerp.shared.configuration.ApplicationContextHolder;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.usermgmt.model.office.Office;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//not in use
public class OfficeCodeGenerator implements IdentifierGenerator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {
        PrefixGenerationUtils prefixGenerationUtils = ApplicationContextHolder.getBean(PrefixGenerationUtils.class);
        Office o = (Office) object;
        Connection connection = session.connection();
        String newCode =  prefixGenerationUtils.getPrefix(o.getOrganisationType(), o.getDefinedCode(), session);
        try(Statement statement = connection.createStatement()) {
            ResultSet rs1 = statement.executeQuery("select name_np from office where code = '" + newCode + "'");
            if(rs1.next()) {
                throw new ServiceValidationException("यो कार्यालय कोड पहिले नै " +
                        rs1.getString(1) +
                        " कार्यालय को लागी प्रयोग गरिएको छ।\n");
            } else {
                return newCode;
            }
        } catch (SQLException e) {
            throw new ServiceValidationException(e.getMessage());
        } finally {
            session.close();
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Issue while closing connection in OfficeCodeGenerator Class");
                    e.printStackTrace();
                }
        }
    }
}
