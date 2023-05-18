package com.gerp.tms.repo;

import com.gerp.tms.model.project.BookMarkProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMarkProjectRepository extends JpaRepository<BookMarkProject,Long> {
    void deleteByProjectIdAndMemberId(Integer projectId, String loginEmployeeCode);

    boolean existsByProjectIdAndMemberId(Integer id, String employeeCode);

}
