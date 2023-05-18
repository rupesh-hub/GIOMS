package com.gerp.shared.generic.api;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface GenericSoftDeleteRepository<T extends BaseEntity, ID extends Serializable> extends
        GenericRepository<T, ID> {

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.isActive = true")
    List<T> findAll();

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id in ?1 and e.isActive = true")
    List<T> findAllById(Iterable<ID> ids);

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.id = ?1 and e.isActive = true")
    T getOne(ID id);

    //Look up deleted entities
    @Query("select e from #{#entityName} e where e.isActive = false")
    @Transactional(readOnly = true)
    List<T> findInactive();

    //Look up all entities
    @Query("select e from #{#entityName} e order by e.createdDate desc ")
    @Transactional(readOnly = true)
    List<T> findAllWithInactive();

//	@Query("select e from #{#entityName} e")
//	@Transactional(readOnly = true)
//	Page<T> findAllWithInactivePageAble(Specification<T> specification, Pageable pageable);

//	@Query("select count(e) from #{#entityName} e where e.id = ?1 and e.isActive = true")
//	long check(ID id);

    @Override
    @Transactional(readOnly = true)
    @Query("select count(e) from #{#entityName} e where e.isActive = true")
    long count();

    @Override
    @Transactional(readOnly = true)
    default boolean existsById(ID id) {
        return getOne(id) != null;
    }

    @Override
    @Query("update #{#entityName} e set e.isActive= case when e.isActive=true then false else true end where e.id = ?1")
    @Transactional
    @Modifying
    void deleteById(ID id);

    @Query("update #{#entityName} e set e.isActive=true where e.id = ?1")
    @Transactional
    @Modifying
    void activeById(ID id);

    @Override
    @Transactional
    default void delete(T entity) {
        deleteById((ID) entity.getId());
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(entitiy -> deleteById((ID) entitiy.getId()));
    }

    @Override
    @Query("update #{#entityName} e set e.isActive=false")
    @Transactional
    @Modifying
    void deleteAll();
}
