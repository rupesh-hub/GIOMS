package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.dto.SalutationPojo;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.group.OfficeGroupPojo;
import com.gerp.dartachalani.dto.kasamu.ServicePojo;
import com.gerp.shared.constants.cacheconstants.EmpCacheConst;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.*;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.pojo.json.ApiDetail;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Component
//@CacheConfig(cacheNames = {"activeFiscalYear","employeeMinimal", "officeDetail","fiscalYearDetails"})
public class UserMgmtServiceData extends BaseController {
    private final UserMgmtProxy userMgmtProxy;

    public UserMgmtServiceData(UserMgmtProxy userMgmtProxy) {
        this.userMgmtProxy = userMgmtProxy;
    }

    @SneakyThrows
//    @Cacheable(value = "activeFiscalYear")
    public IdNamePojo findActiveFiscalYear() {
        ResponseEntity<?> responseEntity = userMgmtProxy.getActiveFiscalYearPojo();
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            IdNamePojo idNamePojo = objectMapper.convertValue(globalApiResponse.getData(), IdNamePojo.class);
            idNamePojo.setId(Long.valueOf(idNamePojo.getCode()));
            return idNamePojo;
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = "dateRangeList")
    public DateListPojo dateRangeList(int year, boolean currentDate) {
        ResponseEntity<?> responseEntity = userMgmtProxy.getDateRangeList(year,currentDate);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
          return objectMapper.convertValue(globalApiResponse.getData(), DateListPojo.class);

        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = "dateRangeListOfYear")
    public List<DateListPojo> dateRangeListOfYear(int year) {
        ResponseEntity<?> responseEntity = userMgmtProxy.getDateRangeListForYear(year);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            List<?> objectList = (List<?>) globalApiResponse.getData();
            return objectList.parallelStream().map(obj->objectMapper.convertValue(obj,DateListPojo.class)).collect(Collectors.toList());
        }
        else {
            return null;
        }
    }
    @SneakyThrows
    @Cacheable(value = "fiscalYearDetails")
    public FiscalYearPojo findFiscalYeaDetails() {
        ResponseEntity<?> responseEntity = userMgmtProxy.getFiscalActiveYear();
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), FiscalYearPojo.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public FiscalYearPojo getFiscalDetailByFiscalYearCode(String fiscalYear) {
        ResponseEntity<?> responseEntity = userMgmtProxy.getFiscalDetailByFiscalYearCode(fiscalYear);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), FiscalYearPojo.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = EmpCacheConst.CACHE_VALUE_MINIMAL, key = EmpCacheConst.CACHE_SET_KEY,condition= EmpCacheConst.CACHE_CONDITION, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public EmployeeMinimalPojo getEmployeeDetailMinimal(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getEmployeeDetailMinimal(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            System.out.println("emp detail minimal: "+new Gson().toJson(globalApiResponse));
            EmployeeMinimalPojo minimalPojo = objectMapper.convertValue(globalApiResponse.getData(), EmployeeMinimalPojo.class);
            return minimalPojo;
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = EmpCacheConst.CACHE_VALUE_DETAIL, key = EmpCacheConst.CACHE_SET_KEY,condition= EmpCacheConst.CACHE_CONDITION, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public EmployeePojo getEmployeeDetail(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getEmployeeDetail(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            EmployeePojo employeeNamePojo = objectMapper.convertValue(globalApiResponse.getData(), EmployeePojo.class);
            return employeeNamePojo;
        }
        else {
            return new EmployeePojo();
        }
    }

    @SneakyThrows
    public List<SectionEmployeePojo> getSectionEmployeeList(Long sectionCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getSectionEmployeeList(sectionCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            List<?> objectList = (List<?>) globalApiResponse.getData();
            return objectList.parallelStream().map(obj->objectMapper.convertValue(obj,SectionEmployeePojo.class)).collect(Collectors.toList());
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public OfficeHeadPojo getOfficeHeadDetail(String officeCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeHead(officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            OfficeHeadPojo officeHeadPojo = objectMapper.convertValue(globalApiResponse.getData(), OfficeHeadPojo.class);
            return officeHeadPojo;
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_DETAIL, key = OfficeCacheConst.CACHE_SET_KEY,condition= "#officeCode !=null", unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public OfficePojo getOfficeDetail(String officeCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeDetail(officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficePojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_MINIMAL, key = OfficeCacheConst.CACHE_SET_KEY,condition= "#officeCode !=null", unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public OfficeMinimalPojo getOfficeDetailMinimal(String officeCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeDetailMinimal(officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficeMinimalPojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_SECTION, key = OfficeCacheConst.CACHE_KEY_SECTION, condition = OfficeCacheConst.CACHE_CONDITION_SECTION, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public SectionPojo getSectionDetail(Long sectionId) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getSectionBySectionId(sectionId);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), SectionPojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_DESIGNATION_POJO, key = OfficeCacheConst.CACHE_KEY_DESIGNATION_POJO, condition = OfficeCacheConst.CACHE_CONDITION_DESIGNATION, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public DesignationPojo getDesignationDetail(String designationCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getDesignationByDesignationCode(designationCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), DesignationPojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_DESIGNATION_DETAIL_POJO, key = OfficeCacheConst.CACHE_KEY_DESIGNATION_DETAIL_POJO, condition = OfficeCacheConst.CACHE_CONDITION_CODE, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public DetailPojo getDesignationDetailByCode(String code) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getDesignationByCode(code);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), DetailPojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_POSITION_POJO, key = OfficeCacheConst.CACHE_KEY_POSITION_POJO, condition = OfficeCacheConst.CACHE_CONDITION_POSITION_CODE, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public PositionPojo getPositionDetail(String positionCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getPositionByPositionCode(positionCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), PositionPojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_SERVICE_POJO, key = OfficeCacheConst.CACHE_KEY_SERVICE_POJO, condition = "#serviceGroupCode != null ", unless = OfficeCacheConst.CACHE_UNLESS_CONDITION)
    public ServicePojo getServiceGroupDetail(String serviceGroupCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getServiceGroupByCode(serviceGroupCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), ServicePojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    public OfficePojo getActiveOfficeStatus(List<String> officeCodes) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getActiveOfficeStatus(officeCodes);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficePojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    public List<SalutationPojo> getSalutationDetail(List<com.gerp.shared.pojo.SalutationPojo> salutationPojos) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getSalutationDetail(salutationPojos);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return (List<SalutationPojo>) globalApiResponse.getData();
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = EmpCacheConst.CACHE_VALUE_DELEGATION_RESPONSE_POJO, key = EmpCacheConst.CACHE_KEY_DELEGATION_RESPONSE_POJO, unless = EmpCacheConst.CACHE_UNLESS_CONDITION, condition = "#id !=null")
    public DelegationResponsePojo getDelegationDetailsById(Integer id) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getDelegationDetailsById(id);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), DelegationResponsePojo.class);
        } else {
            return null;
        }
    }


    @SneakyThrows
    public OfficeTemplatePojo getOfficeTemplate(String officeCode, String type) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeTemplate(officeCode, type);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficeTemplatePojo.class);
        } else {
            return null;
        }
    }

   public OfficeTemplatePojo getOfficeTemplateById(@PathVariable("id") Long id) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeTemplateById(id);
       GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
       if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
           return objectMapper.convertValue(globalApiResponse.getData(), OfficeTemplatePojo.class);
       } else {
           return null;
       }
    }

    public OfficeGroupPojo getOfficeGroupById(@PathVariable("id") Integer id) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeGroupById(id);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficeGroupPojo.class);
        } else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = EmpCacheConst.CACHE_VALUE_EMPLOYEE_SECTION, key = EmpCacheConst.CACHE_SET_PARAMETER_KEY, unless = EmpCacheConst.CACHE_UNLESS_CONDITION, condition = "#pisCode !=null and #sectionId != null")
    public List<String> getPreviousEmployeeDetail(String pisCode,Long sectionId) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getPreviousEmployeeDetail(pisCode,sectionId);
        GlobalApiTestPojo globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiTestPojo.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            if(globalApiResponse.getData()!=null) {
                return  globalApiResponse.getData().stream().map(Object::toString).collect(Collectors.toList());
            }else{
                return null;
            }
        }
        else {
            return null;
        }
    }

    public Boolean isUserAuthorized(@RequestBody ApiDetail apiDetail) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.isUserAuthorized(apiDetail);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), Boolean.class);
        } else {
            return false;
        }
    }
}
