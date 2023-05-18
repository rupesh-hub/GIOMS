package com.gerp.usermgmt.services.orgtransfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;

public interface OrgTransferRequestService {
     Long requestTransfer(OrgTransferRequestPojo orgTransferRequest);
     Long changeTransferAction(OrgTransferRequestPojo orgTransferRequest);

     Page<OrgTransferRequestPojo> requestedTransferPaginated(GetRowsRequest paginatedRequest);

     Page<OrgTransferRequestPojo> allFilteredTransferPaginated(GetRowsRequest paginatedRequest);


     OrgTransferRequestPojo transferDetail(Long id);
     void acknowledgeTransfer(Long id, Long deviceId);

    void update(OrgTransferRequestPojo orgTransferRequest);
}
