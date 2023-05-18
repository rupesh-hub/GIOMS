package com.gerp.usermgmt.services.transfer;

import com.gerp.shared.enums.SalutationTypeConstant;
import com.gerp.shared.pojo.SalutationDetailsPojo;
import com.gerp.shared.pojo.SalutationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.office.OfficeGroup;
import com.gerp.usermgmt.model.office.OfficeGroupDetail;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.SectionPojo;
import com.gerp.usermgmt.services.SalutationService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.office.OfficeGroupingService;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.services.organization.office.SectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class SalutationServiceImpl implements SalutationService {

    private final EmployeeService employeeService;
    private final OfficeGroupingService officeGroupingService;
    private final OfficeService officeService;
    private final SectionService sectionService;

    public SalutationServiceImpl(EmployeeService employeeService, OfficeGroupingService officeGroupingService, OfficeService officeService, SectionService sectionService) {
        this.employeeService = employeeService;
        this.officeGroupingService = officeGroupingService;
        this.officeService = officeService;
        this.sectionService = sectionService;
    }

    @Override
    public List<SalutationPojo> getSalutation(List<SalutationPojo> salutationPojos) {
        salutationPojos.parallelStream().forEach(obj->{
           if ( obj.getType().equals(SalutationTypeConstant.INDIVIDUAL)){
               getEmployeeDetails(obj);
           }else if (obj.getType().equals(SalutationTypeConstant.EXTERNAL_OFFICE)){
                getExternalOffice(obj);
           }else {
                getOfficeDetails(obj);
           }
        });
        return salutationPojos;
    }

    private void getOfficeDetails(SalutationPojo obj) {
        try {
            OfficePojo officePojo = officeService.officeDetail(obj.getPisCode());
            SalutationDetailsPojo salutationDetailsPojo = getSalutationDetailsPojo(officePojo.getNameEn(), officePojo.getNameNp(), officePojo.getCode(), null, null, null);
            salutationDetailsPojo.setLevel(officePojo.getOrganizationLevel());
            salutationDetailsPojo.setDistrict(officePojo.getDistrict());
            obj.setOffice(salutationDetailsPojo);
            setSection(obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getExternalOffice(SalutationPojo obj) {
       try {
           OfficeGroup externalOfficeById = officeGroupingService.getExternalOfficeById( Integer.parseInt(obj.getPisCode()));
           obj.setExternalOffice(getSalutationDetailsPojo(externalOfficeById.getNameEn(),externalOfficeById.getNameNp(),externalOfficeById.getId().toString(),externalOfficeById.getAddress(),externalOfficeById.getEmail(),externalOfficeById.getPhoneNumber()));
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void getEmployeeDetails(SalutationPojo obj) {
       try{
           EmployeeMinimalPojo employeeMinimalPojo = employeeService.employeeDetailMinimal(obj.getPisCode());
           SalutationDetailsPojo salutationDetailsPojo = getSalutationDetailsPojo(employeeMinimalPojo.getEmployeeNameEn(),employeeMinimalPojo.getEmployeeNameNp(),employeeMinimalPojo.getPisCode(),null,null,null);
           obj.setEmployee(salutationDetailsPojo);
           setSection(obj);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void setSection(SalutationPojo obj) {
       try{
           SectionPojo sectionSubsectionById = sectionService.getSectionSubsectionById((long) obj.getSectionId());
           obj.setSection(getSalutationDetailsPojo(sectionSubsectionById.getNameEn(),sectionSubsectionById.getNameNp(),sectionSubsectionById.getId().toString(),null,null,null));
       }catch (Exception e){
          e.printStackTrace();
       }
    }

    private SalutationDetailsPojo getSalutationDetailsPojo(String nameEn,String nameNp,String code,String address,String email,String phoneNumber) {
        return new  SalutationDetailsPojo().builder()
                            .nameEn(nameEn)
                            .code(code)
                            .address(address)
                .email(email)
                .phoneNumber(phoneNumber)
                            .nameNp(nameNp).build();
    }
}
