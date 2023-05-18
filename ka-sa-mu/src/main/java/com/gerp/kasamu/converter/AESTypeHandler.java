package com.gerp.kasamu.converter;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class AESTypeHandler extends BaseTypeHandler<Object> {


    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, o.toString());
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String columnValue = resultSet.getString(columnName);
        return AttributeEncryptor.convertToEntityAttributeStatic(columnValue);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String columnValue = resultSet.getString(columnIndex);
        return AttributeEncryptor.convertToEntityAttributeStatic(columnValue);
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String columnValue = callableStatement.getString(columnIndex);
        return AttributeEncryptor.convertToEntityAttributeStatic(columnValue);
    }
}
