package com.gerp.tms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.tms.pojo.authorization.AccountPojo;
import com.gerp.tms.pojo.authorization.ActivityLevelPojo;
import com.gerp.tms.pojo.authorization.AuthorizationActivityPojo;
import com.gerp.tms.pojo.authorization.HeaderOfficeDetailsPojo;
import com.gerp.tms.pojo.response.ProjectAndTaskStatusPojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Mapper
public interface ProgramMapper {
    List<AuthorizationActivityPojo> getProgramList(@Param("fiscalYearId") String fiscalYearId,@Param("activityName") String activityName);

    Page<ActivityLevelPojo> getActivites(Page<ActivityLevelPojo> pagination,
                                         @Param("fiscalYearId") String fiscalYear,
                                         @Param("activityName") String activityName,
                                         @Param("sortByOrder") String sortByOrder,
                                         @Param("sortBy") String sortBy,
                                         @Param("officeCode") String officeCode,
                                         @Param("accountCode") String accountCode,
                                         @Param("filter") String filter,
                                         @Param("filterByHead") String filterByHead);

    ActivityLevelPojo getActivityById(Integer id);

    List<AccountPojo> getHeadingDetails(@Param("officeCode") String officeCode);



    List<ProjectAndTaskStatusPojo> findProjectByFiscalYear(@Param("fiscalYear") String fiscalYear, @Param("responsibleUnit") String responsibleUnit, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    Page<ProjectResponsePojo> getProjectList(Page<ProjectResponsePojo> responsePojoPage,@Param("fiscalYear") String fiscalYear);
}
