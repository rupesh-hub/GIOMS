package com.gerp.usermgmt.repo.FavouriteContact;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.contact.FavouritesContact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavouriteContactRepo extends GenericRepository<FavouritesContact, Long> {

    @Query("select f from FavouritesContact f where f.user.id = :id and f.employee.pisCode = :pisCode")
    FavouritesContact findByUserAndEmployeeDetail(@Param("id") Long id ,@Param("pisCode") String pisCode);
}
