package com.gerp.usermgmt.controller.contact;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.pojo.organization.FavouriteContactPojo;
import com.gerp.usermgmt.services.organization.contact.FavouriteContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/favourite")
public class FavouriteContactController extends BaseController {

    private  final FavouriteContactService favouriteContactService;
    private final CustomMessageSource customMessageSource;

    public FavouriteContactController(FavouriteContactService favouriteContactService, CustomMessageSource customMessageSource) {
        this.favouriteContactService = favouriteContactService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.USER;
    }


//    @PreAuthorize("{hasPermission(#this.this.permissionName,'create')}")
    @PostMapping(value="/save")
    public ResponseEntity<?> addToFavourite(@RequestBody FavouriteContactPojo favouriteContactPojo) {
        Long id = favouriteContactService.save(favouriteContactPojo);
        if (!ObjectUtils.isEmpty(id)) {

            return ResponseEntity.ok(
                    successResponse("Successfully Added to Favourites", id));
        } else {
            throw new RuntimeException("Unable to add to favourite");
        }
    }

//    @PreAuthorize("{hasPermission(#this.this.permissionName,'delete')}")
    @DeleteMapping(value="/remove/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
         try {
             favouriteContactService.delete(id);

             return ResponseEntity.ok(
                     successResponse("Removed User from Favourites", null));
         }catch (Exception ex){
             throw  new RuntimeException("unable to delete");
         }
        }

//    @PreAuthorize("{hasPermission(#this.this.permissionName,'read')}")
        @GetMapping(value = "/all")
        public ResponseEntity<?> all() {
            return ResponseEntity.ok(
                    successResponse("Successfully fetched",
                            favouriteContactService.getAllFavourites()));

        }

}
