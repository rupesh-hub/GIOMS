package com.gerp.usermgmt.services.organization.fiscalyear;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.fiscalyear.FiscalYear;

import java.util.List;


public interface FiscalYearService extends GenericService<FiscalYear, String> {
    FiscalYear getActiveYear();
    List<IdNamePojo> getALlYear();

    FiscalYearPojo getFiscalYearsDetails(String fiscalYear);
    FiscalYearPojo getFiscalYearsByCode(String fiscalYearCode);
}
