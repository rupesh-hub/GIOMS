package com.gerp.usermgmt.services.transfer.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.mapper.transfer.TransferAuthorityMapper;
import com.gerp.usermgmt.mapper.transfer.TransferMapper;
import com.gerp.usermgmt.model.transfer.*;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.shared.pojo.DateListPojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.pojo.transfer.TransferAuthorityRequestPojo;
import com.gerp.usermgmt.pojo.transfer.TransferAuthorityResponsePojo;
import com.gerp.usermgmt.repo.transfer.*;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.services.transfer.TransferConfigService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransferConfigServiceImpl implements TransferConfigService {
    private final TransferAUthorityRepo transferAUthorityRepo;
    private final TransferAuthorityMapper transferAuthorityMapper;
    private final CustomMessageSource customMessageSource;
    private final TokenProcessorService tokenProcessorService;
    private final TransferAuthorityPositionRepo transferAuthorityPositionRepo;
    private final TransferAuthorityGroupRepo transferAuthorityGroupRepo;
    private final TransferAuthorityTypeRepo transferAuthorityTypeRepo;
    private final TransferAuthorityOfficesRepo transferAuthorityofficesRepo;
    private final TransferMapper transferMapper;
    private final OfficeService officeService;
    private String serviceTop=null;

    public TransferConfigServiceImpl(TransferAUthorityRepo transferAUthorityRepo, TransferAuthorityMapper transferAuthorityMapper, CustomMessageSource customMessageSource, TokenProcessorService tokenProcessorService, TransferAuthorityPositionRepo transferAuthorityPosition, TransferAuthorityGroupRepo transferAuthorityGroupRepo, TransferAuthorityTypeRepo transferAuthorityTypeRepo, TransferAuthorityOfficesRepo transferAuthorityofficesRepo, TransferMapper transferMapper, OfficeService officeService) {
        this.transferAUthorityRepo = transferAUthorityRepo;
        this.transferAuthorityMapper = transferAuthorityMapper;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
        this.transferAuthorityPositionRepo = transferAuthorityPosition;
        this.transferAuthorityGroupRepo = transferAuthorityGroupRepo;
        this.transferAuthorityTypeRepo = transferAuthorityTypeRepo;
        this.transferAuthorityofficesRepo = transferAuthorityofficesRepo;
        this.transferMapper = transferMapper;
        this.officeService = officeService;
    }

    @Override
    public Integer addTransferAuthority(TransferAuthorityRequestPojo transferAuthorityRequestPojo) {
        TransferAuthority transferAuthority = new TransferAuthority();
        convertToTransferAuthority(transferAuthorityRequestPojo, transferAuthority);
        return transferAUthorityRepo.save(transferAuthority).getId();
    }

    @Override
    public List<TransferAuthorityResponsePojo> getTransferConfig() {
        return transferAuthorityMapper.getTransferConfig(tokenProcessorService.getUserId());
    }

    @Override
    public List<DetailPojo> getTransferAuthorityOffice() {
        String employeeServiceCOde = transferAuthorityMapper.getEmployeeServiceCOde(tokenProcessorService.getPisCode());
        String topLevelServiceCode = getServicePojos(employeeServiceCOde);
        List<DetailPojo> transferAuthorityOffices = transferAuthorityMapper.getTransferAuthorityOffices(topLevelServiceCode);
        if (transferAuthorityOffices.size()>0){
            if (transferAuthorityOffices.get(0).getCodes().size() > 0){
                if (transferAuthorityOffices.get(0).getCodes().contains(employeeServiceCOde) || (serviceTop != null && transferAuthorityOffices.get(0).getCodes().contains(serviceTop))){
                    return transferAuthorityOffices;
                }else { return getAuthorityNotSetupOffices(); }
            }else { return transferAuthorityOffices; }
        }
        return getAuthorityNotSetupOffices() ;
    }

    private List<DetailPojo> getAuthorityNotSetupOffices() {
        List<DetailPojo> detailPojoList = new ArrayList<>();
        String ministryCode = getMinistryCode(officeService.findByCode(tokenProcessorService.getOfficeCode()));
        if (!ministryCode.equalsIgnoreCase(tokenProcessorService.getOfficeCode())){
            getUpperLevelOffices(detailPojoList,tokenProcessorService.getOfficeCode(),0);
        }
        detailPojoList.addAll(transferAuthorityMapper.getConfiguredMinistryOffice(null));
        return detailPojoList;
    }

    private List<DetailPojo> getUpperLevelOffices(List<DetailPojo> detailPojoList, String officeCode,int count) {
        DetailPojo office = transferAuthorityMapper.findOffice(officeCode);
        if (office.getDobAd() == null || office.getNameEn().contains("MINISTRY")) {
            detailPojoList.add(office);
            return detailPojoList;
        }
       if (count != 0){
           detailPojoList.add(office);
       }
       return getUpperLevelOffices(detailPojoList,office.getDobAd(),1);
    }

    private String getServicePojos(String code) {

        ServicePojo service = transferMapper.getService(code);
        if (service.getParentCode() != null){
            serviceTop = service.getCode();
        }
        if (service.getParentCode() == null || service.getParentCode().equals("142")){
            return service.getCode();
        }
       return getServicePojos(service.getParentCode()+"");
    }

    @Override
    public Integer updateTransferAuthority(TransferAuthorityRequestPojo transferAuthorityRequestPojo) {
        if (transferAuthorityRequestPojo.getId()== null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.parameterMissing,"id"));
        }
        Optional<TransferAuthority> authority = transferAUthorityRepo.findById(transferAuthorityRequestPojo.getId());

        if (!authority.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Transfer Authority"));
        }
        TransferAuthority transferAuthority = authority.orElse(new TransferAuthority());
        deleteOldData(transferAuthority);
        convertToTransferAuthority(transferAuthorityRequestPojo,transferAuthority);
        transferAUthorityRepo.save(transferAuthority);
        return transferAuthorityRequestPojo.getId();
    }

    private void deleteOldData(TransferAuthority transferAuthority) {
      transferAuthority.getTransferAuthorityPositions().forEach(obj->{
          transferAuthorityPositionRepo.deletePostionById(obj.getId());
      });
      transferAuthority.getTransferAuthorityOffices().forEach(obj->{
          transferAuthorityofficesRepo.deleteOfficeById(obj.getId());
      });
      transferAuthority.getTransferAuthorityGroups().forEach(obj->{
          transferAuthorityGroupRepo.deleteGroupById(obj.getId());
      });
      transferAuthority.getTransferAuthorityTypes().forEach(obj->{
          transferAuthorityTypeRepo.deleteTypeById(obj.getId());
      });
    }

    private void convertToTransferAuthorityForUpdates(TransferAuthorityRequestPojo transferAuthorityRequestPojo, TransferAuthority transferAuthority) {
        transferAuthority.getTransferAuthorityPositions().parallelStream().forEach(obj -> {
            transferAuthorityRequestPojo.getPositions().parallelStream().forEach(obj::setPositionCode);
        });
        if (transferAuthority.getTransferAuthorityPositions().size() != transferAuthorityRequestPojo.getPositions().size()) {
           List<String> positions = new ArrayList<>( transferAuthorityRequestPojo.getPositions());
            for (int i=transferAuthority.getTransferAuthorityPositions().size(); i<transferAuthorityRequestPojo.getPositions().size();i++){
                transferAuthority.getTransferAuthorityPositions().add(new TransferAuthorityPosition(positions.get(i)));
            }
        }
        transferAuthority.getTransferAuthorityTypes().parallelStream().forEach(obj->{
            transferAuthorityRequestPojo.getTransferType().parallelStream().forEach(obj::setType);
        });
        if (transferAuthority.getTransferAuthorityTypes().size() != transferAuthorityRequestPojo.getTransferType().size()) {
            List<String> types = new ArrayList<>( transferAuthorityRequestPojo.getTransferType());
            for (int i=transferAuthority.getTransferAuthorityTypes().size(); i<transferAuthorityRequestPojo.getTransferType().size();i++){
                transferAuthority.getTransferAuthorityTypes().add(new TransferAuthorityType(types.get(i)));
            }
        }
        transferAuthority.getTransferAuthorityGroups().forEach(obj->{
            transferAuthorityRequestPojo.getGroupCode().parallelStream().forEach(obj::setGroupCode);
        });
        if (transferAuthority.getTransferAuthorityGroups().size() != transferAuthorityRequestPojo.getGroupCode().size()) {
            List<String> groups = new ArrayList<>( transferAuthorityRequestPojo.getGroupCode());
            for (int i=transferAuthority.getTransferAuthorityGroups().size(); i<transferAuthorityRequestPojo.getGroupCode().size();i++){
                transferAuthority.getTransferAuthorityGroups().add(new TransferAuthorityGroup(groups.get(i)));
            }
        }
        transferAuthority.setServiceCode(transferAuthorityRequestPojo.getServiceCode());

        transferAuthority.getTransferAuthorityOffices().forEach(obj->{
            transferAuthorityRequestPojo.getOffices().parallelStream().forEach(obj::setOfficeCode);
        });
        if (transferAuthority.getTransferAuthorityOffices().size() != transferAuthorityRequestPojo.getOffices().size()) {
            List<String> offices = new ArrayList<>( transferAuthorityRequestPojo.getOffices());
            for (int i=transferAuthority.getTransferAuthorityOffices().size(); i<transferAuthorityRequestPojo.getOffices().size();i++){
                transferAuthority.getTransferAuthorityOffices().add(new TransferAuthorityOffice(offices.get(i)));
            }
        }
    }
    private void convertToTransferAuthority(TransferAuthorityRequestPojo transferAuthorityRequestPojo, TransferAuthority transferAuthority) {
        transferAuthority.setTransferAuthorityGroups(transferAuthorityRequestPojo.getGroupCode().parallelStream().map(TransferAuthorityGroup::new).collect(Collectors.toList()));
        transferAuthority.setTransferAuthorityTypes(transferAuthorityRequestPojo.getTransferType().parallelStream().map(TransferAuthorityType::new).collect(Collectors.toList()));
        transferAuthority.setTransferAuthorityPositions(transferAuthorityRequestPojo.getPositions().parallelStream().map(TransferAuthorityPosition::new).collect(Collectors.toList()));
        transferAuthority.setServiceCode(transferAuthorityRequestPojo.getServiceCode());
        transferAuthority.setTransferAuthorityOffices(transferAuthorityRequestPojo.getOffices().parallelStream().map(TransferAuthorityOffice::new).collect(Collectors.toList()));
    }


    private TransferAuthority getTransferAuthority(Integer id) {
        Optional<TransferAuthority> transferAuthorityOptional = transferAUthorityRepo.findById(id);
        if (!transferAuthorityOptional.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Transfer Authority"));
        }
       return transferAuthorityOptional.orElse(new TransferAuthority());
    }

    @Override
    public Integer deleteAuthortiyById(Integer id) {
        transferAUthorityRepo.deleteById(id);
//        transferAuthorityMapper.deleteById(id);
        return id;
    }

    @Override
    public Page<DetailPojo> getEmployeeToBeTransfered(String employeeName, int limit, int page, boolean isWithSelected, String officeCode) {
        Page<DetailPojo> detailPojoPage = new Page<>(page,limit);
        List<DetailPojo> configuredMinistryOffice = transferAuthorityMapper.getConfiguredMinistryOffice(null);
        if (tokenProcessorService.getOfficeCode().equalsIgnoreCase(configuredMinistryOffice.get(0).getCode())){
            return transferAuthorityMapper.getEmployeeToBeTransferedWithOffice(detailPojoPage,employeeName,officeCode);
        }

        if (isWithSelected){
            return transferAuthorityMapper.getEmployeeToBeTransferedWithOffice(detailPojoPage,employeeName,tokenProcessorService.getOfficeCode());
        }
        List<DetailPojo> transferAuthorityList = transferAuthorityMapper.findByOfficeCode(tokenProcessorService.getOfficeCode());
        Set<String> positionCodes = new HashSet<>();
        Set<String> serviceCodes = new HashSet<>();
        if (transferAuthorityList != null && !transferAuthorityList.isEmpty()){
            getGroupAndPositionCode(transferAuthorityList, positionCodes, serviceCodes);
            return transferAuthorityMapper.getEmployeeToBeTransferInternalOffices(detailPojoPage,serviceCodes,positionCodes,officeCode,employeeName);
        }else {
            List<DetailPojo> allData = transferAuthorityMapper.findByOfficeCode(null);
           allData.parallelStream().forEach(obj->{
               if (obj.getCodes() != null && !obj.getCodes().isEmpty() ) {
                   serviceCodes.addAll(obj.getCodes());
               } else {
                   serviceCodes.addAll(transferAuthorityMapper.findAllService(obj.getCode()));
               }
           });
            return transferAuthorityMapper.getEmployeeToBeTransferedByOffice(detailPojoPage, tokenProcessorService.getOfficeCode(), employeeName, officeCode,positionCodes,serviceCodes);
        }
    }

    private void getGroupAndPositionCode(List<DetailPojo> transferAuthorityList, Set<String> positionCodes, Set<String> serviceCodes) {
        transferAuthorityList.parallelStream().forEach(obj -> {
            positionCodes.addAll(obj.getPositionCodes());
            if (obj.getCodes() != null && !obj.getCodes().isEmpty()) {
                serviceCodes.addAll(obj.getCodes());
            } else {
                serviceCodes.add(obj.getCode());
            }
        });
    }

    @Override
    public Page<OfficePojo> getTransferOffices(String officeName, int limit, int page, String officeCode, String districtCode) {
        Page<OfficePojo> detailPojoPage = new Page<>(page,limit);
        List<DetailPojo> configuredMinistryOffice = transferAuthorityMapper.getConfiguredMinistryOffice(null);
        if (tokenProcessorService.getOfficeCode().equalsIgnoreCase(configuredMinistryOffice.get(0).getCode())){
            return officeService.allOffices(limit,page,officeName,null);
        }
//        List<DetailPojo> configuredMinistryOffice = transferAuthorityMapper.getConfiguredMinistryOffice(tokenProcessorService.getOfficeCode());
//        if (configuredMinistryOffice != null  && configuredMinistryOffice.size() >0 ){
//            return officeService.allOffices(limit,page,officeName,null);
//        }
        List<DetailPojo> transferAuthorityList = transferAuthorityMapper.findByOfficeCode(tokenProcessorService.getOfficeCode());
        if (transferAuthorityList != null && !transferAuthorityList.isEmpty()){
            Set<String> types = new HashSet<>();
            transferAuthorityList.forEach(obj-> types.addAll(obj.getTypes()));
            if (types.size() == 3){
                return officeService.allOffices(limit,page,officeName,null);
            } else {
                if (types.contains("within") && !types.contains("internal") && !types.contains("outside ministry")) {
                    return transferAuthorityMapper.findTransferOffices(detailPojoPage, officeCode,null,districtCode);
                } else if (types.contains("internal") && !types.contains("outside ministry")) {
                    return transferAuthorityMapper.findTransferOffices(detailPojoPage, null,getMinistryCode(officeService.findByCode(officeCode)),districtCode);
                }else  {
                    return  officeService.allOffices(limit,page,officeName,null);
                }
            }
//                if (types.contains("within") && !types.contains("internal")) {
//                    return transferAuthorityMapper.findTransferOffices(detailPojoPage, officeCode,null);
//                } else  {
//                    return transferAuthorityMapper.findTransferOffices(detailPojoPage, null,getMinistryCode(officeService.findByCode(officeCode)));
//                }
        }else {
            return transferAuthorityMapper.findTransferOffices(detailPojoPage, tokenProcessorService.getOfficeCode(),null,districtCode);
        }
    }

    @Override
    public  Page<OfficePojo> getTransferFromOffice(String officeName, int limit, int page, String districtCode) {
        Page<DetailPojo> detailPojoPage = new Page<>(page,limit);
        List<DetailPojo> configuredMinistryOffice = transferAuthorityMapper.getConfiguredMinistryOffice(null);
        if (tokenProcessorService.getOfficeCode().equalsIgnoreCase(configuredMinistryOffice.get(0).getCode())){
            return officeService.allOffices(limit,page,officeName,districtCode);
        }
        List<DetailPojo> transferAuthorityList = transferAuthorityMapper.findByOfficeCode(tokenProcessorService.getOfficeCode());
        if (transferAuthorityList.isEmpty()){
           return transferAuthorityMapper.getTransferFromOffice(detailPojoPage,officeName,tokenProcessorService.getOfficeCode(),districtCode);
        }
        return officeService.allOffices(limit,page,officeName,districtCode);
    }

    @Override
    public DateListPojo getDateRange(boolean currentDate, int currentFiscalYear) {
        return transferMapper.getDateRange(currentDate? LocalDate.now():null,  currentFiscalYear);
    }

    @Override
    public List<DateListPojo> getYearDateRange(int currentFiscalYear) {
        return transferMapper.getYearDateRange(currentFiscalYear);
    }

    private String getMinistryCode(OfficePojo officePojo){
        if (officePojo.getParentOffice() == null ){
            return officePojo.getCode();
        }
        return getMinistryCode(officePojo.getParentOffice());
    }
}
