package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.shared.constants.cacheconstants.EmpCacheConst;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApiDataListPojo;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.GlobalApiTestPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.pojo.json.ApiDetail;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
@CacheConfig(cacheNames = {"activeFiscalYear","employeeMinimal","officeDetail"})
public class UserMgmtServiceData extends BaseController {
    private static final Logger LOG = Logger.getLogger(UserMgmtServiceData.class);

    private final UserMgmtProxy userMgmtProxy;
    private final UserMgmtMapper userMgmtMapper;

    public UserMgmtServiceData(UserMgmtProxy userMgmtProxy, UserMgmtMapper userMgmtMapper) {
        this.userMgmtProxy = userMgmtProxy;
        this.userMgmtMapper = userMgmtMapper;
    }

    @SneakyThrows
    @Cacheable(value = "activeFiscalYear")
    public IdNamePojo findActiveFiscalYear() {
        String code = userMgmtMapper.getActiveFiscalYearId();
        IdNamePojo idNamePojo = new IdNamePojo();
        idNamePojo.setCode(code);
        idNamePojo.setId(code ==null?null:Long.valueOf(code));
        return idNamePojo;
//        ResponseEntity responseEntity = userMgmtProxy.getActiveFiscalYearPojo();
//        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
//        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
//            IdNamePojo idNamePojo = objectMapper.convertValue(globalApiResponse.getData(), IdNamePojo.class);
//            idNamePojo.setId(Long.valueOf(idNamePojo.getCode()));
//            return idNamePojo;
//        }
//        else {
//            return null;
//        }
    }

    @SneakyThrows
    @Cacheable(value = EmpCacheConst.CACHE_VALUE_MINIMAL,key = EmpCacheConst.CACHE_SET_KEY, unless = EmpCacheConst.CACHE_UNLESS_CONDITION, condition = "#pisCode != null")
    public EmployeeMinimalPojo getEmployeeDetailMinimal(String pisCode) {
        ResponseEntity responseEntity = userMgmtProxy.getEmployeeDetailMinimal(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            EmployeeMinimalPojo minimalPojo = objectMapper.convertValue(globalApiResponse.getData(), EmployeeMinimalPojo.class);
            return minimalPojo;
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT, key = EmpCacheConst.CACHE_SET_KEY, unless = EmpCacheConst.CACHE_UNLESS_CONDITION, condition = "#pisCode !=null")
    public EmployeeDetailsPojo getEmployeeDetail(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getEmployeeDetail(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            EmployeeDetailsPojo employeeNamePojo = objectMapper.convertValue(globalApiResponse.getData(), EmployeeDetailsPojo.class);
            return employeeNamePojo;
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    @Cacheable(value = OfficeCacheConst.CACHE_VALUE_DETAIL_EATT, key = OfficeCacheConst.CACHE_SET_KEY, unless = OfficeCacheConst.CACHE_UNLESS_CONDITION, condition = "#officeCode !=null")
    public OfficePojo getOfficeDetail(String officeCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = userMgmtProxy.getOfficeDetail(officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficePojo.class);
        } else {
            return null;
        }
    }

    public String getEmployeeNepaliName(String pisCode) {
        return userMgmtMapper.getEmployeeNepaliName(pisCode);
    }

    @SneakyThrows
    public SectionMinimalPojo getSectionEmployee(Long sectionCode ) {
        ResponseEntity responseEntity = userMgmtProxy.getSectionEmployee(sectionCode);
        GlobalApiTestPojo globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiTestPojo.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS && !globalApiResponse.getData().isEmpty()) {
            SectionMinimalPojo sectionMinimalPojo = objectMapper.convertValue(globalApiResponse.getData().get(0), SectionMinimalPojo.class);
            return sectionMinimalPojo;
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public List<OfficePojo> getHierarchyOffice(String officeCode ) {
        ResponseEntity responseEntity = userMgmtProxy.getHierarchyOffice(officeCode);
        GlobalApiTestPojo globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiTestPojo.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS && !globalApiResponse.getData().isEmpty()) {
            List<OfficePojo> officeHierarchyPojo = objectMapper.convertValue(globalApiResponse.getData(), List.class);
            if(officeHierarchyPojo == null) {
                LOG.info("no office hierarchy found");
            }
            return officeHierarchyPojo;
        }
        else {
            return null;
        }
    }



    @SneakyThrows
    public List<String> getLowerHiearchyPisCode() {
        ResponseEntity responseEntity = userMgmtProxy.getLowerHierchyPisCode();
        ApiDataListPojo apiDataListPojo = objectMapper.convertValue(responseEntity.getBody(), ApiDataListPojo.class);
        if (apiDataListPojo.getStatus() == ResponseStatus.SUCCESS) {
            return apiDataListPojo.getData();
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public OfficeHeadPojo getOfficeHeadDetail(String officeCode) {
        ResponseEntity responseEntity = userMgmtProxy.getOfficeHead(officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            OfficeHeadPojo officeHeadPojo = objectMapper.convertValue(globalApiResponse.getData(), OfficeHeadPojo.class);
            return officeHeadPojo;
        }
        else {
            return null;
        }
    }

//    @Cacheable(value = "parentOfficeCodeSelf",key = "#officeCode")
    public List<String> getParentOfficeCodeWithSelf(String officeCode) {
        return userMgmtMapper.getParentOfficeCodeWithSelf(officeCode);
    }


    public List<String> getLowerOfficeCode(String officeCode) {
        return userMgmtMapper.getLowerOffice(officeCode);
    }

    public List<String> getLowerActiveOfficeCode(String officeCode) {
        return userMgmtMapper.getLowerOfficeList(officeCode);
    }
    public List<String> getAllActiveOfficeCode(){
        //TODO use actual query
        return userMgmtMapper.getAllActiveOfficeCode();
//        return Arrays.asList("1623","8655","20768","10112","20782");
    }

    public List<String> getAllActivePisCodeByOffice(String officeCode) {
        return userMgmtMapper.getAllActivePisCodeByOffice(officeCode);
    }

    public String getOfficeCodeByPisCode(String pisCode) {
        return userMgmtMapper.getOfficeCodeByPisCode(pisCode);
    }

    @SneakyThrows
//    @Cacheable(value = "sectionList")
    public List<SectionNamePojo> findSectionList() {
        List<SectionNamePojo>sectionNamePojos=new ArrayList<>();
        ResponseEntity responseEntity = userMgmtProxy.getSectionList();
        GlobalApiTestPojo globalApiTestPojo = objectMapper.convertValue(responseEntity.getBody(), GlobalApiTestPojo.class);
        if (globalApiTestPojo.getStatus() == ResponseStatus.SUCCESS) {

        for (Object x:globalApiTestPojo.getData()){
            SectionNamePojo sectionNamePojo = objectMapper.convertValue(x, SectionNamePojo.class);
            sectionNamePojos.add(sectionNamePojo);

        }

            return sectionNamePojos;
        } else {
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
