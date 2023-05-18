package com.gerp.usermgmt.services.organization.contact;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.contact.FavouritesContact;
import com.gerp.usermgmt.pojo.organization.FavouriteContactPojo;

import java.util.List;

public interface FavouriteContactService extends GenericService<FavouritesContact, Long> {

    Long save(FavouriteContactPojo favouriteContactPojo);

    void delete(Long id);

    List<FavouriteContactPojo> getAllFavourites();
}
