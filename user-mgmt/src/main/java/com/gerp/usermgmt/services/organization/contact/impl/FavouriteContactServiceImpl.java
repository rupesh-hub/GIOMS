package com.gerp.usermgmt.services.organization.contact.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.model.contact.FavouritesContact;
import com.gerp.usermgmt.pojo.organization.FavouriteContactPojo;
import com.gerp.usermgmt.repo.FavouriteContact.FavouriteContactRepo;
import com.gerp.usermgmt.repo.auth.UserRepo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.services.organization.contact.FavouriteContactService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavouriteContactServiceImpl extends GenericServiceImpl<FavouritesContact, Long> implements FavouriteContactService {

    private final FavouriteContactRepo favouriteContactRepo;
    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private EmployeeMapper employeeMapper;

    public FavouriteContactServiceImpl(FavouriteContactRepo favouriteContactRepo) {
        super(favouriteContactRepo);
        this.favouriteContactRepo = favouriteContactRepo;
    }

    @Override
    public Long save(FavouriteContactPojo favouriteContactPojo) {
        FavouritesContact favouriteContact = new FavouritesContact();
        favouriteContact.setUser(userRepo.findById(tokenProcessorService.getUserId()).orElseThrow(() ->  new ResponseStatusException(HttpStatus.UNAUTHORIZED)));
       favouriteContact.setEmployee(employeeRepo.findById(favouriteContactPojo.getPisCode()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
       if(favouriteContactRepo.findByUserAndEmployeeDetail(tokenProcessorService.getUserId(), favouriteContactPojo.getPisCode()) != null){
           throw new RuntimeException("Already Added to favourites");
       }
        return favouriteContactRepo.save(favouriteContact).getId();
    }

    @Override
    public void delete(Long id) {
        if(!ObjectUtils.isEmpty(favouriteContactRepo.getOne(id)))
         favouriteContactRepo.deleteById(id);
        else
            throw  new RuntimeException("Contact not found");
    }

    @Override
    public List<FavouriteContactPojo> getAllFavourites() {
        return employeeMapper.getActiveContact(tokenProcessorService.getUserId());
    }
}
