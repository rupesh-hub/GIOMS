package com.gerp.tms.converter;

import com.gerp.tms.pojo.OfficeDetailPojo;
import com.gerp.tms.proxy.EmployeeDetailsProxy;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OfficeDetailTyeHandler extends BaseTypeHandler<Object> {

    private final EmployeeDetailsProxy employeeDetailsProxy;

    public OfficeDetailTyeHandler(EmployeeDetailsProxy employeeDetailsProxy) {
        this.employeeDetailsProxy = employeeDetailsProxy;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, o.toString());
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String columnValue = resultSet.getString(columnName);
        if (columnValue == null){
            return null;
        }
        return convertToOfficeName(columnValue);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet;
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement;
    }

    private String convertToOfficeName(String code) {
        OfficeDetailPojo officeDetailPojo = employeeDetailsProxy.getOfficeDetail(code);
        if (officeDetailPojo != null) {
            return officeDetailPojo.getNameNp();
        }
        return code;
    }
}
