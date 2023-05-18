package com.gerp.usermgmt.config.generator;

import com.gerp.shared.configuration.ApplicationContextHolder;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;

//public class DesignationCodeGenerator implements IdentifierGenerator {
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    private static final String pref = "GM_";
//    @Override
//    public Serializable generate(SharedSessionContractImplementor session, Object object)
//            throws HibernateException {
//
//        FunctionalDesignation functionalDesignation = (FunctionalDesignation) object;
//        PrefixGenerationUtils prefixGenerationUtils = ApplicationContextHolder.getBean(PrefixGenerationUtils.class);
//        Connection connection = session.connection();
//        try (Statement statement = connection.createStatement()) {
//
//            ResultSet rs = statement.executeQuery("select max(code::int) as Id from functional_designation");
//            Random r = new Random();
//            if (rs.next()) {
//                String id = rs.getString(1).replaceAll("\\s+","");
//                if (id.contains("_")) {
//                    id = String.valueOf((Integer.parseInt(id.substring(id.lastIndexOf("_") + 1)) + r.nextInt(1)));
//
//                } else {
//                    id = String.valueOf(Integer.parseInt(id) + GeneratorUtils.getRandomNonZeroNumber());
//                }
//                return id;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        finally {
//            session.close();
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                log.error("Issue while closing connection in OfficeCodeGenerator Class");
//                e.printStackTrace();
//            }
//        }
//        return functionalDesignation.getCode();
//    }
//}

public class DesignationCodeGenerator extends SequenceStyleGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return super.generate(session, object).toString();
    }


    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);
    }
}
