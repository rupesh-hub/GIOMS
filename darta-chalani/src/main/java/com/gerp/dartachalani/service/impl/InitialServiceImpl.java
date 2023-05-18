package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.config.exception.CustomException;
import com.gerp.dartachalani.dto.DCNumberPojo;
import com.gerp.dartachalani.dto.InitialPojo;
import com.gerp.dartachalani.dto.OfficePojo;
import com.gerp.dartachalani.model.number.ChalaniNumber;
import com.gerp.dartachalani.model.number.DartaNumber;
import com.gerp.dartachalani.model.number.TippaniNumber;
import com.gerp.dartachalani.repo.ChalaniNumberRepo;
import com.gerp.dartachalani.repo.DartaNumberRepo;
import com.gerp.dartachalani.repo.TippaniNumberRepo;
import com.gerp.dartachalani.service.InitialService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class InitialServiceImpl implements InitialService {

    private final DartaNumberRepo dartaNumberRepo;
    private final ChalaniNumberRepo chalaniNumberRepo;
    private final TippaniNumberRepo tippaniNumberRepo;
    private final TokenProcessorService tokenProcessorService;
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private DateConverter dateConverter;

    public InitialServiceImpl(DartaNumberRepo dartaNumberRepo,
                              ChalaniNumberRepo chalaniNumberRepo,
                              TippaniNumberRepo tippaniNumberRepo,
                              TokenProcessorService tokenProcessorService) {
        this.dartaNumberRepo = dartaNumberRepo;
        this.chalaniNumberRepo = chalaniNumberRepo;
        this.tokenProcessorService = tokenProcessorService;
        this.tippaniNumberRepo = tippaniNumberRepo;
    }

    @Override
    public void save(DCNumberPojo data) throws CustomException {

        if (data.getChalaniNo() == null && data.getDartaNo() == null) {
            throw new RuntimeException("Invalid");
        }

        System.out.println("officeCode: " + tokenProcessorService.getOfficeCode());
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        if (data.getChalaniNo() != null) {

            if (data.getChalaniNo().intValue() <= 0)
                throw new RuntimeException("chalani number must be greater than zero");

            this.checkIfExistChalani(tokenProcessorService.getOfficeCode(), fiscalYear.getCode());

            ChalaniNumber chalaniNumber = new ChalaniNumber().builder()
                    .officeCode(tokenProcessorService.getOfficeCode())
                    .fiscalYearCode(fiscalYear.getCode())
                    .dispatchNo(data.getChalaniNo() - 1)
                    .build();

            chalaniNumberRepo.save(chalaniNumber);
        }

        if (data.getDartaNo() != null) {

            if (data.getDartaNo().intValue() <= 0)
                throw new RuntimeException("darta number must be greater than zero");

            this.checkIfExistDarta(tokenProcessorService.getOfficeCode(), fiscalYear.getCode());
            DartaNumber dartaNumber = new DartaNumber().builder()
                    .officeCode(tokenProcessorService.getOfficeCode())
                    .fiscalYearCode(fiscalYear.getCode())
                    .registrationNo(data.getDartaNo() - 1)
                    .build();

            dartaNumberRepo.save(dartaNumber);
        }

    }

    @Override
    public String getDartaNumber(String officeCode) {
        return this.getDarta(officeCode);
    }

    @Override
    public String getChalaniNumber(String sectionCode) {
        return this.getChalani(sectionCode);
    }

    @Override
    public Long getMemoNumber(String officeCode) {
        return this.getTippani(officeCode);
    }

    @Override
    public InitialPojo getByOfficeCode(String officeCode) {
        InitialPojo initialPojo = new InitialPojo();

        String fiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();

        Long darta = dartaNumberRepo.getDartaNumByOfficeCode(officeCode, fiscalYearCode);
        Long chalani = chalaniNumberRepo.getChalaniNumByOfficeCode(officeCode, fiscalYearCode);

        initialPojo.setChalaniNumber(chalani);
        initialPojo.setDartaNumber(darta);

        return initialPojo;
    }

    private void checkIfExistChalani(String officeCode, String fiscalYearCode) throws CustomException {
        if (!chalaniNumberRepo.getByOfficeCode(officeCode, fiscalYearCode).isEmpty()) {
            throw new RuntimeException("Chalani Number Already Set");
        }
    }

    private void checkIfExistDarta(String officeCode, String fiscalYearCode) throws CustomException {
        if (!dartaNumberRepo.getByOfficeCode(officeCode, fiscalYearCode).isEmpty()) {
            throw new RuntimeException("Darta Number Already Set");
        }
    }

    private synchronized String getDarta(String officeCode) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        OfficePojo officePojo = null;
        if (officeCode != null)
            officePojo = userMgmtServiceData.getOfficeDetail(officeCode);

        if (officePojo != null && officePojo.getIsGiomsActive() != null && !officePojo.getIsGiomsActive()) {
            return null;
        }

        // Get a darta by office code and active fiscal year
        DartaNumber dartaNumber = null;
        Long regNumber = null;
        String nepaliRegNum = null;
        synchronized (officeCode) {
            dartaNumber = dartaNumberRepo.getADarta(officeCode, fiscalYear.getCode());

            // If dartaNumber does not exist create a new one
            if (dartaNumber == null) {
                dartaNumber = new DartaNumber().builder()
                        .officeCode(officeCode)
                        .fiscalYearCode(fiscalYear.getCode())
                        .registrationNo(1L)
                        .build();
                dartaNumberRepo.save(dartaNumber);
                regNumber = dartaNumber.getRegistrationNo(); // if present get the darta number
            } else {
                regNumber = dartaNumber != null ? dartaNumber.getRegistrationNo() + 1 : null; // if present get the darta number
                dartaNumber.setRegistrationNo(dartaNumber.getRegistrationNo() == null ? 1L : regNumber);
                dartaNumberRepo.save(dartaNumber);
            }

            //increase te darta number by 1
            nepaliRegNum = dateConverter.convertBSToDevnagari(regNumber != null ? regNumber.toString() : "1");

        }

        return nepaliRegNum;
    }

    private synchronized String getChalani(String sectionCode) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

        ChalaniNumber chalaniNumber = null;
        String a = "";
        synchronized (a) {
            // Get chalani number by office code and active fiscal year
            chalaniNumber = chalaniNumberRepo.getAChalani(tokenProcessorService.getOfficeCode(), fiscalYear.getCode());

            // If it does not exist create a new one
            if (chalaniNumber == null) {
                chalaniNumber = new ChalaniNumber().builder()
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .fiscalYearCode(fiscalYear.getCode())
                        .dispatchNo(1L)
                        .build();

                chalaniNumber = chalaniNumberRepo.save(chalaniNumber);

            }
        }

        String regNumber = (chalaniNumber.getDispatchNo() + 1) + ""; // If exists get a chalani number

        chalaniNumber.setDispatchNo(chalaniNumber.getDispatchNo() + 1);

        // Increment the chalani number by 1
        chalaniNumberRepo.save(chalaniNumber);

        if (sectionCode != null && !sectionCode.equals("")) {
            return sectionCode + "/" + regNumber;
        }
        return regNumber;
    }

    private synchronized Long getTippani(String officeCode) {
        IdNamePojo activeFiscalYear = userMgmtServiceData.findActiveFiscalYear();

        TippaniNumber tippaniNumber = null;

        synchronized (officeCode) {
            tippaniNumber = tippaniNumberRepo.getATippani(officeCode, activeFiscalYear.getCode());
        }
        // If tippaniNumber does not exist create a new one
        if (tippaniNumber == null) {
            tippaniNumber = new TippaniNumber().builder()
                    .officeCode(officeCode)
                    .fiscalYearCode(activeFiscalYear.getCode())
                    .memoNo(1L)
                    .build();
            tippaniNumberRepo.save(tippaniNumber);
        } else {
            tippaniNumber.setMemoNo(tippaniNumber.getMemoNo() == null ? 1L : tippaniNumber.getMemoNo() + 1);
            tippaniNumberRepo.save(tippaniNumber);
        }

        return tippaniNumber.getMemoNo();
    }
}
