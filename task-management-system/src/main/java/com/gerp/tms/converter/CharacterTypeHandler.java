package com.gerp.tms.converter;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterTypeHandler extends BaseTypeHandler<Character> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Character character, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, character.toString());
    }

    @Override
    public Character getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String columnValue = resultSet.getString(columnName);
        if (columnValue != null && !columnValue.isEmpty() ) {
            return columnValue.charAt(0);
        } else {
            return null;
        }
    }

    @Override
    public Character getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String columnValue = resultSet.getString(columnIndex);
        if (columnValue != null && !columnValue.isEmpty() ) {
            return columnValue.charAt(0);
        } else {
            return null;
        }
    }

    @Override
    public Character getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String columnValue = callableStatement.getString(columnIndex);
        if (columnValue != null && !columnValue.isEmpty() ) {
            return columnValue.charAt(0);
        } else {
            return null;
        }
    }
}
