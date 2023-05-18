package com.gerp.usermgmt.config.generator;

import com.gerp.shared.configuration.ApplicationContextHolder;
import com.gerp.usermgmt.model.office.Office;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.query.NativeQuery;

import java.io.Serializable;
import java.math.BigInteger;

public class OfficeCodeGeneration extends SequenceStyleGenerator
{
    private static final String NEXTVAL_QUERY = "SELECT nextval('office_code_seq')";
    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {
        PrefixGenerationUtils prefixGenerationUtils = ApplicationContextHolder.getBean(PrefixGenerationUtils.class);
        Office o = (Office) object;
        String prefix =  prefixGenerationUtils.getPrefix(o.getOrganisationType(), session);
        NativeQuery nextvalQuery = session.createSQLQuery(NEXTVAL_QUERY);
        BigInteger value = (BigInteger) nextvalQuery.setFlushMode(FlushMode.COMMIT).uniqueResult();
        return prefix + "_" + value;
    }
}
