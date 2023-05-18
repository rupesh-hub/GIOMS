package com.gerp.usermgmt.services.transfer.impl;

import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.pojo.transfer.TransferRequestForOfficePojo;

import java.util.List;

public interface SaruwaService {
    Long generateSaruwa(Long id);

    Long generateSaruwaLetter(Long id);

    Long generateRawana(Long id);
}
