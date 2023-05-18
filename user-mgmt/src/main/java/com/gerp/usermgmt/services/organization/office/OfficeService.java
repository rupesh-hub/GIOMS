package com.gerp.usermgmt.services.organization.office;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.pojo.external.TMSOfficePojo;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeSavePojo;

import java.util.List;

public interface OfficeService extends GenericService<Office, String> {
    OfficePojo findByCode(String officeCode);
    OfficePojo officeDetail(String officeCode);
    List<OfficePojo> getAllTopParentOffice();

    List<String> getLowerOfficeEmployee(String officeCode);

    Page<OfficePojo> allOffices(int limit, int page, String searchKey, String districtCode);

    Page<OfficePojo> allOfficePaginated(GetRowsRequest paginatedRequest);
    OfficePojo officeSectionListByCode(String officeCode);

    List<OfficePojo> officeLowerHierarchyList(String officeCode);
    List<OfficePojo> officeHigherHierarchyListOnly(String officeCode);

    List<OfficePojo> officeHigherHierarchyListOnly();

    List<OfficePojo> getChildOffice(String officeCode);
    List<OfficePojo> getParentOffices(String officeCode);
    List<OfficePojo> getMinistryOffices();

    List<OfficePojo> officeListByParams(SearchPojo searchPojo);


    List<OfficePojo> officeActiveStatus(List<String> officeCode);

    String saveOffice(OfficeSavePojo officePojo);

    String updateOffice(OfficeSavePojo officePojo);

    OfficePojo officeMinimalDetail(String officeCode);
    boolean activateOffice(String officeCode);

    void updateSetupStatus();

    boolean getSetUpStatus();

    List<TMSOfficePojo> getAllGIOMSActiveOffice();

    List<TMSOfficePojo> getAllOfficeByOfficeCodes(List<String> offcieCodes);

    List<OfficePojo> getGiomsActiveOfficeList();
}
