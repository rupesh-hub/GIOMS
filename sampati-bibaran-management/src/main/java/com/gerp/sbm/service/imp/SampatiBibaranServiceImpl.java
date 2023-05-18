package com.gerp.sbm.service.imp;

import com.gerp.sbm.Helper.RSAUtil;
import com.gerp.sbm.Proxy.EmployeeDetailProxy;
import com.gerp.sbm.Proxy.NepaliDate;
import com.gerp.sbm.Proxy.NepaliDateProxy;
import com.gerp.sbm.model.assets.*;
import com.gerp.sbm.model.sampati.SampatiMaster;

import com.gerp.sbm.pojo.FiscalYearData;
import com.gerp.sbm.pojo.RequestPojo.*;
import com.gerp.sbm.pojo.ResponsePojo.*;
import com.gerp.sbm.repo.*;
import com.gerp.sbm.service.SampatiBibaranService;
import com.gerp.sbm.token.TokenProcessorService;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.google.gson.Gson;


import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SampatiBibaranServiceImpl implements SampatiBibaranService {

//    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDqDmRxJjPU5HhEsI5xJeB8i5LsJBQYtnUq4/5NhJLqq3oKjlGMnVyC0Hl24HMKVPzWCFyt8XKU/cRXekui7u4Jp5epCi+sb+4tW7dXpsefhaP/mreykRbQ1yMj8qDRtuYe9v7A6xPyJnaDS16PmFDeKayb1uQ8aucPd9IoFl7QRQIDAQAB";
//    private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOoOZHEmM9TkeESwjnEl4HyLkuwkFBi2dSrj/k2EkuqregqOUYydXILQeXbgcwpU/NYIXK3xcpT9xFd6S6Lu7gmnl6kKL6xv7i1bt1emx5+Fo/+at7KRFtDXIyPyoNG25h72/sDrE/ImdoNLXo+YUN4prJvW5Dxq5w930igWXtBFAgMBAAECgYAyUDW3PQcXVKWl3m5AVGwVWcuTh43qYg590dTwxPbwCzyI2F8fYxRn6nix3T3dkrdnKxUBL036STLTuy5XMBVrBJ/dHYiIQC/Bh57rguLPPvfrBOwxfSuo467QQmur42HW6RyVeQ/s5G8VKjtyrKYg819NeRl9GsymQ2YxOHtHAQJBAPpA7sJw6KNbL0oP8xDpVtSjZx78ARbsm4ZhW5e44ivyWAliLElibWR7ATHWLelpp7PI1syTN1Tb8jCmmA+yUIkCQQDvbj8NWTCrbeQ0PoXXwJbehNkV/LQRsbrsmeELwJIV+Bi4DR/Lm5rGarBN2wJRROA6SFoKC2tZnAfoToNnHnrdAkEAiS8LM3/dp58W30y6/CxNsycYIn4WwtBVuWapZRSut1SUOiCdEmVofkqHryAv7uJCUmXoiiCxlk00CMNqXEBtsQJBALKs2BrTA1H7CQC/48vVKgFWrFYPJiXKN09XqizGAX6pjXDUQnUEYyVM+tKu7HTPbHGyCpdSXEMcAH6pEMCEa3UCQCbk15BKAwadgUslcWl5GTWNEmpdC+znVIoap2QWGnP89RFhIUAMa6UICbW0Vf8Dxnpv3Cn992G4DT6PiTITaDk=";

    private static String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAiU+lMijKk07f/m5MpH7xSDLzpV/5b8Kj9C9WKrMCS7pW/bEvSVonPWxg0Z8tjhWpp47Hmbyn5kgl0Nnig+Ng0PvgsN1HlcyvQpRtDE3Fk4AXnq4nAlJw6LiPz/7uAr7aqnSS0qEC2FHXgKhotqW3k7cv3NQUmPcNYDZ5PnyKF/+KPRicprwlY8++5jzmFcmvAPaNPUzMJ8f3na96MiqLaE/JHzzdIrj4D9lH3N9RN8kqS1UJTQUAxUONrDSFRMbttuJt9dxKRTZAUaqawwt0b757b9gDusbdoy9Q7dl5da8ySIPF6qzdAMNrYmovnnSB5+JVBfdMswyre22ITlpBkpB950+rIacTw/e1H8J+1SODZRhq807JM1c3Cpi7yP396TEHIjbkwwPfZzL4NL3YKGvj1q9IwgQViG5gBJ51KwUswOijFzifCAnieOzcUgw4qmQjg2kBDlYnTppQUL0CAo55SYJiCpreFkJyHajuhwO2DV3/GGq6rNXfO0/yV+jsC1m0mVYigcXeonJ2VS03sV+QzY+F3TMId8iQVbLvet2aH2spB8agxMiX4rPTHEvWxhvKfRhS+4TpeNxtED0qZO0Y6B7d6kdOwNLEttTxY1Q2Hi92iNomrV3Cx0R1FtdxT63W3QWPCLbgG5XitF+20P6qvSmwip9NIy6+z1fLBu0CAwEAAQ==";

//private static String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAiU+lMijKk07f/m5MpH7xSDLzpV/5b8Kj9C9WKrMCS7pW/bEvSVonPWxg0Z8tjhWpp47Hmbyn5kgl0Nnig+Ng0PvgsN1HlcyvQpRtDE3Fk4AXnq4nAlJw6LiPz/7uAr7aqnSS0qEC2FHXgKhotqW3k7cv3NQUmPcNYDZ5PnyKF/+KPRicprwlY8++5jzmFcmvAPaNPUzMJ8f3na96MiqLaE/JHzzdIrj4D9lH3N9RN8kqS1UJTQUAxUONrDSFRMbttuJt9dxKRTZAUaqawwt0b757b9gDusbdoy9Q7dl5da8ySIPF6qzdAMNrYmovnnSB5+JVBfdMswyre22ITlpBkpB950+rIacTw/e1H8J+1SODZRhq807JM1c3Cpi7yP396TEHIjbkwwPfZzL4NL3YKGvj1q9IwgQViG5gBJ51KwUswOijFzifCAnieOzcUgw4qmQjg2kBDlYnTppQUL0CAo55SYJiCpreFkJyHajuhwO2DV3/GGq6rNXfO0/yV+jsC1m0mVYigcXeonJ2VS03sV+QzY+F3TMId8iQVbLvet2aH2spB8agxMiX4rPTHEvWxhvKfRhS+4TpeNxtED0qZO0Y6B7d6kdOwNLEttTxY1Q2Hi92iNomrV3Cx0R1FtdxT63W3QWPCLbgG5XitF+20P6qvSmwip9NIy6+z1fLBu0CAwEAAQ==";

    private static String privateKey = "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";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private FixedAssetRepo fixedAssetRepo;
    @Autowired
    private BankDetailsRepo bankDetailsRepo;

    @Autowired
    private ShareRepo shareRepo;
    @Autowired
    private LoanRepo loanRepo;
    @Autowired
    private OtherDetailsRepo otherDetailsRepo;

    @Autowired
    private ValuableItemsRepository valuableItemsRepository;

    @Autowired
    private  AgricultureDetailRepo agricultureDetailRepo;
    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private  SampatiMasterRepo sampatiMasterRepo;

    @Autowired
    public RSAUtil rsaUtil;

    @Autowired
    EmployeeDetailProxy<Object> employeeDetailProxy;

    @Autowired
    private Gson gson;
    @Override
    public boolean addFixedAsset(FixedAssetRequestPojo fixedAssetRequestPojo) {

        FiscalYearData data=getFiscalYear();
        try{
            FixedAsset asset=modelMapper.map(fixedAssetRequestPojo,FixedAsset.class);
            asset.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(fixedAssetRequestPojo),publicKey)));
            asset.setFiscal_year_eng(data.getYear());
            asset.setFiscal_year_nep(data.getYearNp());

            fixedAssetRepo.save(asset);
        }catch (Exception ex){
            System.out.print(ex.toString());
        }
        return true;
    }


    private FiscalYearData getFiscalYear() {
        System.out.println("rread me"+employeeDetailProxy.getActiveFiscalYear());
        return modelMapper.map(employeeDetailProxy.getActiveFiscalYear(),FiscalYearData.class);
    }

    @Override
    public boolean editFixedAsset(FixedAssetRequestPojo fixedAssetRequestPojo, Long id) {
        FiscalYearData data=getFiscalYear();
        FixedAsset asset=fixedAssetRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try{
            asset.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(fixedAssetRequestPojo),publicKey)));
            asset.setFiscal_year_eng(data.getYear());
            asset.setFiscal_year_nep(data.getYearNp());
            fixedAssetRepo.save(asset);
        }catch(Exception ex){
            System.out.print(ex.toString());
        }
        return true;
    }

    @Override
    public List<FixedAssetResponsePojo> listAsset(String piscode) {

        List<FixedAsset> fixedAssetList=fixedAssetRepo.findAllByPiscode(piscode);

        List<FixedAssetResponsePojo> data=fixedAssetList.stream().map(e->{
            FixedAssetResponsePojo temp=new FixedAssetResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setFixedAssetRequestPojo(gson.fromJson(content,FixedAssetRequestPojo.class));
            }catch (Exception ex){
                    ex.printStackTrace();
            }
            return  temp;
        }).collect(Collectors.toList());
        return data;
    }

    @Override
    public boolean addBankDetails(BankDetailsRequestPojo bankDetailsRequestPojo) {

        FiscalYearData data=getFiscalYear();
        try{
            BankDetails bankDetails=modelMapper.map(bankDetailsRequestPojo,BankDetails.class);
            bankDetails.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(bankDetailsRequestPojo),publicKey)));
            bankDetails.setFiscal_year_eng(data.getYear());
            bankDetails.setFiscal_year_nep(data.getYearNp());
            bankDetailsRepo.save(bankDetails);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean editBankDetails(BankDetailsRequestPojo bankDetailsRequestPojo, Long id) {

        BankDetails bankDetails=bankDetailsRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST));
       try{
           bankDetails.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(bankDetailsRequestPojo),publicKey)));

           FiscalYearData data=getFiscalYear();
           bankDetails.setFiscal_year_eng(data.getYear());
           bankDetails.setFiscal_year_nep(data.getYearNp());
           bankDetailsRepo.save(bankDetails);
       }catch (Exception ex){
           ex.printStackTrace();
       }
        return false;
    }

    @Override
    public List<BankDetailsResponsePojo> listBankDetails(String piscode) {

        List<BankDetails> bankDetails=bankDetailsRepo.findAllByPiscode(piscode);

        List<BankDetailsResponsePojo> data=bankDetails.stream().map(e->{
            BankDetailsResponsePojo temp=new BankDetailsResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setBankDetailsRequestPojo(gson.fromJson(content,BankDetailsRequestPojo.class));
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return  temp;
        }).collect(Collectors.toList());
        return data;
    }

    @Override
    public boolean addShare(ShareRequestPojo shareRequestPojo) {
        Share share=modelMapper.map(shareRequestPojo,Share.class);
        try{
            share.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(shareRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            share.setFiscal_year_eng(data.getYear());
            share.setFiscal_year_nep(data.getYearNp());

            shareRepo.save((share));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean editShare(ShareRequestPojo shareRequestPojo, Long id) {
        Share share=shareRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
       try{
           share.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(shareRequestPojo),publicKey)));

           FiscalYearData data=getFiscalYear();
           share.setFiscal_year_eng(data.getYear());
           share.setFiscal_year_nep(data.getYearNp());
           shareRepo.save(share);
       }catch(Exception ex){
           ex.printStackTrace();
       }
        return true;
    }

    @Override
    public List<ShareResponsePojo> listShare(String piscode) {
        List<Share> shareList=shareRepo.findAllByPiscode(piscode);

        List<ShareResponsePojo> shareResponsePojos=shareList.stream().map(e->{
           ShareResponsePojo temp=new ShareResponsePojo();
           temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setShareRequestPojo(gson.fromJson(content,ShareRequestPojo.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return temp;
        }).collect(Collectors.toList());
        return shareResponsePojos;
    }

    @Override
    public boolean addLoan(LoanRequestPojo loanRequestPojo) {
        Loan loan=modelMapper.map(loanRequestPojo,Loan.class);
        try{
            loan.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(loanRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            loan.setFiscal_year_eng(data.getYear());
            loan.setFiscal_year_nep(data.getYearNp());
            loanRepo.save((loan));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean editLoan(LoanRequestPojo loanRequestPojo,Long id) {
        Loan loan=loanRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try{
            loan.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(loanRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            loan.setFiscal_year_eng(data.getYear());
            loan.setFiscal_year_nep(data.getYearNp());

            loanRepo.save(loan);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<LoanResponsePojo> listLoan(String piscode) {
        List<Loan> loanList=loanRepo.findAllByPiscode(piscode);

        List<LoanResponsePojo> shareResponsePojos=loanList.stream().map(e->{
            LoanResponsePojo temp=new LoanResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setLoanRequestPojo(gson.fromJson(content,LoanRequestPojo.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return temp;
        }).collect(Collectors.toList());
        return shareResponsePojos;
    }

    @Override
    public boolean addOtherDetails(OtherDetailRequestPojo otherDetailRequestPojo) {
        OtherDetail otherDetail=modelMapper.map(otherDetailRequestPojo,OtherDetail.class);
        try{
            otherDetail.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(otherDetailRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            otherDetail.setFiscal_year_eng(data.getYear());
            otherDetail.setFiscal_year_nep(data.getYearNp());
            otherDetailsRepo.save((otherDetail));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean editOtherDetails(OtherDetailRequestPojo otherDetailRequestPojo, Long id) {
        OtherDetail otherDetail=otherDetailsRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try{
            otherDetail.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(otherDetailRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            otherDetail.setFiscal_year_eng(data.getYear());
            otherDetail.setFiscal_year_nep(data.getYearNp());
            otherDetailsRepo.save(otherDetail);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<OtherDetailResponsePojo> listOtherDetails(String piscode) {
        List<OtherDetail> otherDetailList=otherDetailsRepo.findAllByPiscode(piscode);

        List<OtherDetailResponsePojo> otherDetailResponsePojos=otherDetailList.stream().map(e->{
            OtherDetailResponsePojo temp=new OtherDetailResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setOtherDetailRequestPojo(gson.fromJson(content,OtherDetailRequestPojo.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return temp;
        }).collect(Collectors.toList());
        return otherDetailResponsePojos;
    }

    @Override
    public boolean addValuableItems(ValuableItemsRequestPojo valuableItemsRequestPojo) {
        ValuableItems valuableItems=modelMapper.map(valuableItemsRequestPojo,ValuableItems.class);
        try{
            valuableItems.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(valuableItemsRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            valuableItems.setFiscal_year_eng(data.getYear());
            valuableItems.setFiscal_year_nep(data.getYearNp());
            valuableItemsRepository.save((valuableItems));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean editValuableItems(ValuableItemsRequestPojo valuableItemsRequestPojo, Long id) {
        ValuableItems valuableItems=valuableItemsRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try{
            valuableItems.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(valuableItemsRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            valuableItems.setFiscal_year_eng(data.getYear());
            valuableItems.setFiscal_year_nep(data.getYearNp());
            valuableItemsRepository.save(valuableItems);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<ValuableItemsResponsePojo> listValuableItems(String piscode) {
        List<ValuableItems> valuableItemsList=valuableItemsRepository.findAllByPiscode(piscode);

        List<ValuableItemsResponsePojo> valuableItemsResponsePojos=valuableItemsList.stream().map(e->{
            ValuableItemsResponsePojo temp=new ValuableItemsResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setValuableItemsRequestPojo(gson.fromJson(content,ValuableItemsRequestPojo.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return temp;
        }).collect(Collectors.toList());
        return valuableItemsResponsePojos;
    }

    @Override
    public boolean addAgricultureDetail(AgricultureDetailRequestPojo agricultureDetailRequestPojo) {
        AgricultureDetail agricultureDetail=modelMapper.map(agricultureDetailRequestPojo,AgricultureDetail.class);
        try{
            agricultureDetail.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(agricultureDetailRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            agricultureDetail.setFiscal_year_eng(data.getYear());
            agricultureDetail.setFiscal_year_nep(data.getYearNp());
            agricultureDetailRepo.save((agricultureDetail));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean editAgricultureDetail(AgricultureDetailRequestPojo agricultureDetailRequestPojo, Long id) {
        AgricultureDetail agricultureDetail=agricultureDetailRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try{
            agricultureDetail.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(agricultureDetailRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            agricultureDetail.setFiscal_year_eng(data.getYear());
            agricultureDetail.setFiscal_year_nep(data.getYearNp());
            agricultureDetailRepo.save(agricultureDetail);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<AgricultureDetailResponsePojo> listAgricultureDetail(String piscode) {
        List<AgricultureDetail> agricultureDetailList=agricultureDetailRepo.findAllByPiscode(piscode);

        List<AgricultureDetailResponsePojo> agricultureDetailResponsePojos=agricultureDetailList.stream().map(e->{
            AgricultureDetailResponsePojo temp=new AgricultureDetailResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setAgricultureDetailRequestPojo(gson.fromJson(content,AgricultureDetailRequestPojo.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return temp;
        }).collect(Collectors.toList());
        return agricultureDetailResponsePojos;
    }

    @Override
    public boolean addVehicle(VehicleRequestPojo vehicleRequestPojo) {
        Vehicle vehicle=modelMapper.map(vehicleRequestPojo,Vehicle.class);
        try{
            vehicle.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(vehicleRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            vehicle.setFiscal_year_eng(data.getYear());
            vehicle.setFiscal_year_nep(data.getYearNp());

            vehicleRepo.save((vehicle));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean editVehicle(VehicleRequestPojo vehicleRequestPojo, Long id) {
        Vehicle vehicle=vehicleRepo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try{
            vehicle.setContent(Base64.getEncoder().encodeToString(rsaUtil.encrypt(gson.toJson(vehicleRequestPojo),publicKey)));

            FiscalYearData data=getFiscalYear();
            vehicle.setFiscal_year_eng(data.getYear());
            vehicle.setFiscal_year_nep(data.getYearNp());

            vehicleRepo.save(vehicle);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<VehicleResponsePojo> listVehicle(String piscode) {
        List<Vehicle> vehicleList=vehicleRepo.findAllByPiscode(piscode);

        List<VehicleResponsePojo> vehicleResponsePojoList=vehicleList.stream().map(e->{
            VehicleResponsePojo temp=new VehicleResponsePojo();
            temp.setId(e.getId());
            try{
                String content=rsaUtil.decrypt(e.getContent(),privateKey);
                temp.setVehicleRequestPojo(gson.fromJson(content,VehicleRequestPojo.class));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return temp;
        }).collect(Collectors.toList());
        return vehicleResponsePojoList;
    }


    public Boolean saveSampati(String piscode) {


        List<Map<String,Object>> other_properties=sampatiMasterRepo.findOtherProperties(piscode);


        //if (other_properties.size()==0) throw new ResponseStatusException(HttpStatus.NO_CONTENT,"no content found for employee with this piscode.");

        Set<String> agriculture= other_properties.stream().map(e-> e.get("ad_content")!=null?e.get("ad_content").toString():"").collect(Collectors.toSet());

        Set<String> bank_details= other_properties.stream().map(e-> e.get("bd_content")!=null?e.get("bd_content").toString():"").collect(Collectors.toSet());

        Set<String> fixed_asset= other_properties.stream().map(e-> e.get("fa_content")!=null?e.get("fa_content").toString():"").collect(Collectors.toSet());

        Set<String> loan= other_properties.stream().map(e-> e.get("loan_content")!=null?e.get("loan_content").toString():"").collect(Collectors.toSet());

        Set<String> other_details= other_properties.stream().map(e-> e.get("od_content")!=null?e.get("od_content").toString():"").collect(Collectors.toSet());

        Set<String> share_content= other_properties.stream().map(e-> e.get("share_content")!=null?e.get("share_content").toString():"").collect(Collectors.toSet());

        Set<String> valuable_item= other_properties.stream().map(e->e.get("vi_content")!=null? e.get("vi_content").toString():"").collect(Collectors.toSet());

        Set<String> vehicle= other_properties.stream().map(e-> e.get("v_content")!=null?e.get("v_content").toString():"").collect(Collectors.toSet());

        FiscalYearData data=getFiscalYear();



        SampatiMaster master=new SampatiMaster();
        master.setEmp_pis_code(piscode);

        master.setAgricultureDetailList(gson.toJson(agriculture));
        master.setBankDetailsList(gson.toJson(bank_details));
        master.setFixedAssetList(gson.toJson(fixed_asset));
        master.setLoanList(gson.toJson(loan));
        master.setOtherDetailList(gson.toJson(other_details));
        master.setShareList(gson.toJson(share_content));
        master.setValuableItemsList(gson.toJson(valuable_item));
        master.setVehicleList(gson.toJson(vehicle));

        master.setStatus(true);
        master.setFiscal_year_eng(data.getYear());
        master.setFiscal_year_nep(data.getYearNp());

        Object o= employeeDetailProxy.getEmployeeDetailMinimal(piscode);
        System.out.println("complete data: "+o.toString());

        master.setStatus(true);


        try{

            JSONObject json = new JSONObject(gson.toJson(o));

            master.setFull_name_eng(json.getJSONObject("data").has("nameEn")?json.getJSONObject("data").getString("nameEn"):"");
            master.setFull_name_nep(json.getJSONObject("data").has("nameNp")?json.getJSONObject("data").getString("nameNp"):"");

            master.setPanNo(json.getJSONObject("data").has("pan")?json.getJSONObject("data").getString("pan"):"");
            System.out.println("deg : "+json.getJSONObject("data").getJSONObject("functionalDesignation").getString("name"));
            System.out.println("office_name_eng : "+json.getJSONObject("data").getJSONObject("office").getString("name"));
            System.out.println("temp_district_name : "+json.getJSONObject("data").getJSONObject("office").getString("name"));
//            master.setDesignation(());
            //master.setOfficeCode(json.getJSONObject("data").has("office")?json.getJSONObject("data").getJSONObject("office").getString("code"):"");
            //sampatiMasterRepo.save(master);
        }catch (Exception ex){
            System.out.println(ex.toString());
           throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED,ex.getCause().getLocalizedMessage());
        }
        return true;
    }

    @Override
    public void deleteFixedAsset(Long id) {
        fixedAssetRepo.deleteById(id);

    }

    @Override
    public void deleteBankDetails(Long id) {
        bankDetailsRepo.deleteById(id);
    }

    @Override
    public void deleteShare(Long id) {
        shareRepo.deleteById(id);
    }

    @Override
    public void deleteLoan(Long id) {
        loanRepo.deleteById(id);
    }

    @Override
    public void deleteOtherDetails(Long id) {
        otherDetailsRepo.deleteById(id);
    }

    @Override
    public void deleteValuableItems(Long id) {
            valuableItemsRepository.deleteById(id);
    }

    @Override
    public void deleteAgricultureDetail(Long id) {
            agricultureDetailRepo.deleteById(id);
    }

    @Override
    public void deleteVehicle(Long id) {
        vehicleRepo.deleteById(id);
    }


    @Autowired
    private NepaliDateProxy nepaliDateProxy;

    @Override
    public List<Users> user() {
//        FiscalYearData data=getFiscalYear();
//        Object o=employeeDetailProxy.getEmployeeByOffice();
//        List<Users> users= modelMapper.map(o,new TypeToken<List<Users>>() {}.getType());
//        System.out.println("check my data " +employeeDetailProxy.getEmployeeByOffice());
//        //employeeDetailProxy.getEmployeeByOffice();
//        List<String> existUser=sampatiMasterRepo.findAlreadyExistUser(data.getYear(),data.getYearNp());
//        //existUser.forEach(e->System.out.println("here i am "+e));
//        List<Users> validUser=users.stream().map(e->{
//              if(existUser.contains(e.getPiscode())){
//                   e.setStatus(true) ;
//              }else{
//                   e.setStatus(false);
//              }
//              return e;
//        }).collect(Collectors.toList());
//        return validUser;
//    }
        nepaliDateProxy.date();

        return null;

    }

}
