package com.gerp.usermgmt.services.orgtransfer;

import com.gerp.usermgmt.enums.TransferStatus;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferHistory;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferHistoryPojo;

import java.util.List;
import java.util.Map;

public interface OrgTransferHistoryService {

    void saveHistory(OrgTransferHistory orgTransferHistory);
    void updateHistoryStatus(TransferStatus transferStatus, Long transferId, Boolean acknowledged, String employeePisCode);
    List<OrgTransferHistoryPojo> transferHistory(String pisCode);
    List<Map<String,Object>>  getTransferHistory(String pisCode);
}
