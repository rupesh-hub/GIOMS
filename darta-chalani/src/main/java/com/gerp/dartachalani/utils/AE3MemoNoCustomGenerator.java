package com.gerp.dartachalani.utils;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.tuple.ValueGenerator;

import java.text.SimpleDateFormat;

public class AE3MemoNoCustomGenerator implements ValueGenerator<String> {
    private static final String NEXTVAL_QUERY = "SELECT nextval('ae3dispatch_seq46')";
//        private static final String NEXTVAL_QUERY = sequence_1.nextval;

    private final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
    @Override
    public String generateValue(Session session, Object owner) {
        NativeQuery nextvalQuery = session.createSQLQuery(NEXTVAL_QUERY);
        Object value = nextvalQuery.setFlushMode(FlushMode.COMMIT).uniqueResult();
        return value.toString();
    }
}
