package com.gerp.usermgmt.repo;

import com.gerp.usermgmt.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepo extends JpaRepository<Module, Long> {
    Optional<Module> findByKey(String moduleKey);

    @Query(value = "select * from module where screen_id=?1", nativeQuery = true)
    List<Module> findModuleListByIndividualScreen(Long screenId);

    @Query(value = " select m from Module m order by m.id")
    List<Module> findAllAndOrderById();





}
