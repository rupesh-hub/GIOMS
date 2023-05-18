package com.gerp.shared.generic.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity, ID extends Serializable> extends
        JpaSpecificationExecutor<T>,
        JpaRepository<T, ID> {

//	@Query("select count(e) from #{#entityName} e where e.id = ?1 and e.isActive = true")
//	long check(ID id);
}
