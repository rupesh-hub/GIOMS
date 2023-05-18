package com.gerp.usermgmt.services.organization.employee;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.pojo.employee.EmployeeSectionPojo;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferRequest;
import com.gerp.usermgmt.pojo.external.TMSClientDetailRequestPojo;
import com.gerp.usermgmt.pojo.external.TMSEmployeePojo;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    KararEmployeeChildPojo employeeAllDetail(String pisCode);
    List<Employee> getAllEmployees();
    List<EmployeeMinimalPojo> getAllEmployeesWithHigherOrder();
    List<EmployeeMinimalPojo> getAllEmployeesWithHigherOrderWithoutRole();
    List<EmployeeMinimalPojo> getAllEmployeesWithHigherOrderForEAttendance();
    List<EmployeePojo> getAllEmployeesWithLowerOrder();
    List<EmployeePojo> getAllEmployeesUserWithLowerOrder();
    List<EmployeeMinimalPojo> sectionLowerEmployees(Long sectionId);
    List<EmployeeMinimalPojo> getAllEmployeesWithLowerOrderMinimal();
    EmployeePojo employeeDetail(String pisCode);
    EmployeePojo employeeDetail();
    List<EmployeePojo> employeeListOfOffice();
    List<EmployeePojo> distinctEmployeeListOfOffice();
    Employee detail(String pisCode);

    List<EmployeeSectionPojo> getOfficeSectionEmployeeList();

    EmployeeMinimalPojo employeeDetailMinimal(String pisCode);
    List<EmployeeSectionPojo> getSectionEmployeeList(Long id);
    List<EmployeePojo> getEmployeeListBySectionId(Long id);

    List<EmployeeMinimalPojo> getHigherSectionEmployeeList(Long id);

    Employee saveEmployee(KararEmployeePojo employeePojo);

    void updateUserOffice(String pisCode, String officeCode, String oldOfficeCode);
    Employee updateEmployeeOffice(OrgTransferRequest orgTransferRequest);

    Employee updateEmployee(EmployeePojo employeePojo);
    Employee updateKararEmployee(KararEmployeePojo employeePojo) throws InvocationTargetException, IllegalAccessException;

    EmployeeMinimalPojo getOfficeHeadEmployee(String officeCode);

    List<EmployeePojo> employeeListOfByOffice(String officeCode);

    List<EmployeePojo> employeeAllDetailListOfByOffice(String officeCode);

    void saveEmployeeOnSection(List<String> employeeIds , Long sectionId);

    List<EmployeePojo> searchEmployees(SearchPojo search);

    Page<EmployeePojo> employeeContact(GetRowsRequest paginatedRequest);
    Page<EmployeePojo> employeesPaginated(GetRowsRequest paginatedRequest);

    // can be used same as employeeContact function
    Page<EmployeePojo> employeeContactAllOffice(GetRowsRequest paginatedRequest);

    void updateEmployeeOrders(EmployeePojo employee);
    void activateUnAssignedEmployee(EmployeePojo employee);
    void updatePisCode(EmployeePojo employeePojo, String pisCode);
    EmployeeMinimalPojo getCurrentUser();

    void promoteEmployeeJobDetail(EmployeePromotionPojo employeePromotion);

    List<EmployeeErrorMessagePojo> uploadExcel(MultipartFile file);

    List<EmployeeMinimalPojo> getSpecialEmployeeList();

    Page<EmployeePojo> employeesAdminPaginated(GetRowsRequest paginatedRequest);
    List<EmployeePojo> employeesAdminPrint(String fromDate,String toDate);
    List<EmployeeMinimalPojo> getKararEmployeeList(int days);

    Map<String,Object> getKararEmployeeCount(int days);

    List<TMSEmployeePojo> getEmployeeListDetailByOffice(String officeCode);

    List<TMSEmployeePojo> getAllEmployeeList();

    List<TMSEmployeePojo> getAllEmployeeById(List<Long> userIds);

    TMSEmployeePojo getClientIdByPisCode(TMSClientDetailRequestPojo tmsClientDetailRequestPojo);

    Boolean saveProfilePic(EmployeeMinimalPojo profilePic);

    String saveEmployeeForWizard(KararEmployeePojo kararEmployeePojo);

    EmployeePojo employeeDetailFromEmployeeCode(String employeeCode);


    List<EmployeeMinimalPojo> getAllEmployeesWithPrivilllege();

}
