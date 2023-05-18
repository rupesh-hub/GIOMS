package com.gerp.usermgmt.repo.address;


import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.address.Address;

public interface AddressRepo extends GenericSoftDeleteRepository<Address, Long> {
}
