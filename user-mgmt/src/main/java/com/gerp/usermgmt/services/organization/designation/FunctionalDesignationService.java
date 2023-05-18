package com.gerp.usermgmt.services.organization.designation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.FunctionalDesignationPojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;

import java.util.List;

public interface FunctionalDesignationService extends GenericService<FunctionalDesignation, String> {
    List<IdNamePojo> designationSearch(SearchPojo searchPojo);
    List<IdNamePojo> officeDesignationSearch(SearchPojo searchPojo);
    List<IdNamePojo> officeDesignationList(String officeCode);

    List<IdNamePojo> findSectionDesignationById(Long id);

    void deleteDesignation(String id);
    String save(FunctionalDesignationPojo functionalDesignationPojo);
    String update(FunctionalDesignationPojo functionalDesignationPojo);

    Page<FunctionalDesignationPojo> getFilterPaginated(GetRowsRequest paginatedRequest);

    //get designation detail by designation code
    DetailPojo getDesignationByCode(String code);
}
