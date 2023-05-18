package com.gerp.usermgmt.services.organization.Address.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.address.Address;
import com.gerp.usermgmt.repo.address.AddressRepo;
import com.gerp.usermgmt.services.organization.Address.AddressService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressServiceImpl extends GenericServiceImpl<Address, Long> implements AddressService {
    private final AddressRepo addressRepo;

    public AddressServiceImpl(AddressRepo addressRepo) {
        super(addressRepo);
        this.addressRepo = addressRepo;
    }
}
