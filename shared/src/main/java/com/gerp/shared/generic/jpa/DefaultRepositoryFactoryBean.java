package com.gerp.shared.generic.jpa;

import com.gerp.shared.generic.api.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class DefaultRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable>
        extends JpaRepositoryFactoryBean<T, S, ID> {

    /**
     * Returns a {@link RepositoryFactorySupport}.
     *
     * @param entityManager
     * @return
     */
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new RepositoryFactory(entityManager);
    }

    public DefaultRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    private static class RepositoryFactory<T extends BaseEntity, I extends Serializable> extends JpaRepositoryFactory {

        private EntityManager entityManager;

        public RepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            return new GenericRepositoryImpl<T, I>((Class<T>) information.getDomainType(), entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            // The RepositoryMetadata can be safely ignored, it is used by the
            // JpaRepositoryFactory
            // to check for QueryDslJpaRepository's which is out of scope.
            return GenericRepositoryImpl.class;
        }
    }
}
