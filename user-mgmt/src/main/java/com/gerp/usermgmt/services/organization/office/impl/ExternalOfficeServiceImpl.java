//package com.gerp.usermgmt.services.organization.office.impl;
//
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.utils.CrudMessages;
//import com.gerp.usermgmt.mapper.organization.OfficeMapper;
//import com.gerp.usermgmt.model.office.ExternalOffice;
//import com.gerp.usermgmt.pojo.organization.office.ExternalOfficePojo;
//import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
//import com.gerp.usermgmt.repo.office.ExternalOfficeRepo;
//import com.gerp.usermgmt.services.organization.office.ExternalOfficeService;
//import lombok.SneakyThrows;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class ExternalOfficeServiceImpl implements ExternalOfficeService{
//
//    private final ExternalOfficeRepo externalOfficeRepo;
//    private final CustomMessageSource customMessageSource;
//    private final OfficeMapper officeMapper;
//
//    public ExternalOfficeServiceImpl(ExternalOfficeRepo externalOfficeRepo, CustomMessageSource customMessageSource, OfficeMapper officeMapper) {
//        this.externalOfficeRepo = externalOfficeRepo;
//        this.customMessageSource = customMessageSource;
//        this.officeMapper = officeMapper;
//    }
//
//    @Override
//    @SneakyThrows
//    public int addExternalOffice(ExternalOfficePojo externalOfficePojo) {
//        ExternalOffice externalOffice = new ExternalOffice();
//        BeanUtils.copyProperties(externalOffice,externalOfficePojo);
//        externalOfficeRepo.save(externalOffice);
//        return externalOffice.getId();
//    }
//
//    @Override
//    public int updateExternalOffice(ExternalOfficePojo dto) {
//        if (dto.getId() == null){
//            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Id"));
//        }
//        ExternalOffice externalOffice = getExternalOffice(dto.getId());
//        externalOffice.setEmail(dto.getEmail());
//        externalOffice.setAddress(dto.getAddress());
//        externalOffice.setNameEn(dto.getNameEn());
//        externalOffice.setNameNp(dto.getNameNp());
//        externalOffice.setPhoneNumber(dto.getPhoneNumber());
//        return dto.getId();
//    }
//
//    @Override
//    public List<OfficePojo> getExternalOffice() {
//        return officeMapper.getExternalOffice();
//    }
//
//    private ExternalOffice getExternalOffice(int id) {
//        Optional<ExternalOffice> externalOfficeOptional = externalOfficeRepo.findById(id);
//        if (!externalOfficeOptional.isPresent()){
//            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"External Office"));
//        }
//       return externalOfficeOptional.orElse(new ExternalOffice());
//    }
//
//
//}
