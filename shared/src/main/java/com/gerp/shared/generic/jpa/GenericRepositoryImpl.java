package com.gerp.shared.generic.jpa;

import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.generic.api.GenericRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


@NoRepositoryBean
public class GenericRepositoryImpl<T extends BaseEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements GenericRepository<T, ID>, Serializable {
    private static final long serialVersionUID = -3943656263780592037L;
    @PersistenceContext
    private EntityManager entityManager;

    // There are two constructors to choose from, either can be used.
    public GenericRepositoryImpl(Class<T> domainClass,
                                 EntityManager entityManager) {
        super(domainClass, entityManager);

        // This is the recommended method for accessing inherited class
        // dependencies.
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

//	@Override
//	public long check(ID id) {
//		return 0;
//	}
}
