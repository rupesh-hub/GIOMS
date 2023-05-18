package com.gerp.attendance.generator;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

import java.math.BigInteger;

public class UniqueIdGenerator implements ValueGenerator<Long> {

    private static final String NEXTVAL_QUERY = "SELECT nextval('uniqueapproval_seq')";

    @Override
    public Long generateValue(Session session, Object owner) {
        NativeQuery nextvalQuery = session.createSQLQuery(NEXTVAL_QUERY);
        BigInteger value = (BigInteger) nextvalQuery.setFlushMode(FlushMode.COMMIT).uniqueResult();
        return value.longValue();
    }
}
