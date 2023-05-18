package com.gerp.usermgmt.services;

import com.gerp.shared.pojo.SalutationPojo;

import java.util.List;

public interface SalutationService {
    List<SalutationPojo> getSalutation(List<SalutationPojo> salutationPojos);
}
