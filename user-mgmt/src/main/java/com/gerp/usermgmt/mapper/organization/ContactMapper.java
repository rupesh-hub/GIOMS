package com.gerp.usermgmt.mapper.organization;

import com.gerp.usermgmt.pojo.organization.FavouriteContactPojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContactMapper {

    List<FavouriteContactPojo> getFavouritesContact(String pisCode);
}
