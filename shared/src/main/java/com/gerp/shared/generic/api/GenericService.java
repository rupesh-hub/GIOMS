package com.gerp.shared.generic.api;

import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.api.specification.SpecificationBuilder;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GenericService<T extends BaseEntity, ID extends Serializable> {
    int NUMBER_OF_PERSONS_PER_PAGE = 20;

    /**
     * Find previously persisted object by it's id
     */
    public T findById(ID id);

    public boolean isExist(ID id);

    /**
     * Search previously persisted objects by a sql query string and query arguments
     * The result can be obtained in page manner by using firstResult and maxResults
     */

    /**
     * Find all
     */
    public List<ID> findAllIds(Iterable<ID> idattribute);

    /**
     * Find all
     */
    public List<T> getAll();

    public List<T> findMany(int firstResult, int maxResults);

    /**
     * Get row count of rows in table
     */
    public long getRowCount();

    public List<T> executeAnyQuery(String query, final Map<String, Object> args, int firstResult, int maxResults, boolean sql);

    /**
     * Persist the newInstance object into database
     */
    public abstract T create(T newInstance);

    /**
     * Save changes made to a persistent object.
     */
    public abstract T update(T instance);

    /**
     * Remove an object from persistent storage in the database
     */
    public abstract void delete(T persistentObject);

    /**
     * Remove an object from persistent storage in the database by id
     */
    public abstract void deleteById(ID id);

    public abstract void activeById(ID id);

    /**
     * Remove an object from persistent storage in the database
     */
    public abstract void deleteAll();

    public abstract void createMany(Collection<T> newInstances);

    public abstract void saveMany(Collection<T> instances);

    /**
     * Save changes made to a persistent object.
     */
    public abstract void updateMany(Collection<T> instances);

    String[] findObjectStatusValues();

    public abstract List<T> findInactive();

    public abstract List<T> findActive();

    public abstract List<T> findAllWithInactive();

    public abstract SpecificationBuilder<T> getSpecificationBuilder(GetRowsRequest getRowsRequest);

    public abstract SpecificationBuilder<T> getSpecificationBuilderNative(GetRowsRequest getRowsRequest);

    public abstract Page<T> getPaginated(GetRowsRequest getRowsRequest);

//	public abstract Page<T> getPaginatedInactive(GetRowsRequest getRowsRequest);

//	public abstract boolean checkIdValidity(ID id);

}
