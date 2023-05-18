package com.gerp.tms.repo;

import com.gerp.tms.model.task.TaskProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskProgressStatusRepository extends JpaRepository<TaskProgressStatus, Long> {

    Optional<TaskProgressStatus> findByStatusName(String statusName);

    @Query("select t from TaskProgressStatus t where t.createdBy=:loginEmployeeCode or t.createdBy is null ")
    List<TaskProgressStatus> findAllByCreatedBy(@Param("loginEmployeeCode") String loginEmployeeCode);

    Optional<TaskProgressStatus> findByStatusNameAndCreatedBy(String toUpperCase, long parseInt);

}
