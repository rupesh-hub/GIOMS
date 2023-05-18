package com.gerp.usermgmt.mapper.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePromotionPojo;
import com.gerp.usermgmt.pojo.organization.jobdetail.DesignationDetailPojo;
import com.gerp.usermgmt.pojo.organization.jobdetail.JobDetailPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeJobDetailMapper {

    Page<EmployeePromotionPojo> searchCurrentPromotionLog(
            Page<EmployeePromotionPojo> page,
            @Param("searchField") Map<String, Object> searchField,
            @Param("officeCode") String officeCode);

    List<DesignationDetailPojo> findAllOfficeHistory(@Param("pisCode") String pisCode);

    LocalDate getStartDate(@Param("officeCoe") String officeCode, @Param("pisCode") String pisCode);

    LocalDate  getEndDate(@Param("officeCoe") String officeCode, @Param("pisCode") String pisCode);

    DesignationDetailPojo getServiceDetails(@Param("officeCode") String officeCode, @Param("pisCode") String pisCode);


}
