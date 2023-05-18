package com.gerp.sbm.service;

import com.gerp.sbm.model.assets.ValuableItems;
import com.gerp.sbm.pojo.RequestPojo.*;
import com.gerp.sbm.pojo.ResponsePojo.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface SampatiBibaranService {
    boolean addFixedAsset(FixedAssetRequestPojo fixedAssetRequestPojo) ;

    boolean editFixedAsset(FixedAssetRequestPojo fixedAssetRequestPojo, Long id);

    List<FixedAssetResponsePojo> listAsset(String piscode);

    boolean addBankDetails(BankDetailsRequestPojo bankDetailsRequestPojo);

    boolean editBankDetails(BankDetailsRequestPojo bankDetailsRequestPojo, Long id);

    List<BankDetailsResponsePojo> listBankDetails(String piscode);

    boolean addShare(ShareRequestPojo shareRequestPojo);

    boolean editShare(ShareRequestPojo shareRequestPojo, Long id);

    List<ShareResponsePojo> listShare(String piscode);

    boolean addLoan(LoanRequestPojo loanRequestPojo);

    boolean editLoan(LoanRequestPojo loanRequestPojo,Long id);

    List<LoanResponsePojo> listLoan(String piscode);

    boolean addOtherDetails(OtherDetailRequestPojo otherDetailRequestPojo);

    boolean editOtherDetails(OtherDetailRequestPojo otherDetailRequestPojo, Long id);

    List<OtherDetailResponsePojo> listOtherDetails(String piscode);

    boolean addValuableItems(ValuableItemsRequestPojo valuableItemsRequestPojo);

    boolean editValuableItems(ValuableItemsRequestPojo valuableItemsRequestPojo, Long id);

    List<ValuableItemsResponsePojo> listValuableItems(String piscode);

    boolean addAgricultureDetail(AgricultureDetailRequestPojo agricultureDetailRequestPojo);

    boolean editAgricultureDetail(AgricultureDetailRequestPojo agricultureDetailRequestPojo, Long id);

    List<AgricultureDetailResponsePojo> listAgricultureDetail(String piscode);

    boolean addVehicle(VehicleRequestPojo vehicleRequestPojo);

    boolean editVehicle(VehicleRequestPojo vehicleRequestPojo, Long id);

    List<VehicleResponsePojo> listVehicle(String piscode);

    Boolean saveSampati(String piscode);

    void deleteFixedAsset(Long id);

    void deleteBankDetails(Long id);

    void deleteShare(Long id);

    void deleteLoan(Long id);

    void deleteOtherDetails(Long id);

    void deleteValuableItems(Long id);

    void deleteAgricultureDetail(Long id);

    void deleteVehicle(Long id);

    List<Users> user();

}
