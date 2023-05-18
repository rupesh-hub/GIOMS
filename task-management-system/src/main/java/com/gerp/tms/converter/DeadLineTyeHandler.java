package com.gerp.tms.converter;

import com.gerp.tms.model.report.Months;
import com.gerp.tms.repo.MonthRepo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DeadLineTyeHandler extends BaseTypeHandler<Object> {


    private final MonthRepo monthRepo;

    public DeadLineTyeHandler(MonthRepo monthRepo) {
        this.monthRepo = monthRepo;
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
        return convertToMonth(columnValue);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet;
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement;
    }


    private String convertToMonth(String date) {
        String npDate = date.substring(date.indexOf("-")+1,date.lastIndexOf("-"));
       if (npDate.contains("0")){
         npDate=  npDate.substring(npDate.indexOf("0")+1);
       }
       return monthRepo.findById(Integer.parseInt(npDate)).orElse(new Months()).getNameNp();
    }
}
