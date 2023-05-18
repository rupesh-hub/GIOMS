package com.gerp.usermgmt.services.transfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.shared.pojo.DateListPojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.pojo.transfer.TransferAuthorityRequestPojo;
import com.gerp.usermgmt.pojo.transfer.TransferAuthorityResponsePojo;

import java.util.List;

public interface TransferConfigService {
    Integer addTransferAuthority(TransferAuthorityRequestPojo transferAuthorityRequestPojo);

    List<TransferAuthorityResponsePojo> getTransferConfig();

    List<DetailPojo> getTransferAuthorityOffice();

    Integer updateTransferAuthority(TransferAuthorityRequestPojo transferAuthorityRequestPojo);

    Integer deleteAuthortiyById(Integer id);

    Page<DetailPojo> getEmployeeToBeTransfered(String employeeName, int limit, int page, boolean isWithSelected, String officeCode);

    Page<OfficePojo> getTransferOffices(String officeName, int limit, int page, String officeCode, String districtCode);

    Page<OfficePojo> getTransferFromOffice(String officeName, int limit, int page, String districtCode);

    DateListPojo getDateRange(boolean currentDate, int currentFiscalYear);

    List<DateListPojo> getYearDateRange(int currentFiscalYear);
}
