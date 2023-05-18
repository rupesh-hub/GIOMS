package com.gerp.usermgmt.services.transfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.transfer.TransferRequestForOfficePojo;
import com.gerp.usermgmt.pojo.transfer.TransferRequestPojo;
import com.gerp.usermgmt.pojo.transfer.TransferRequestToTransferPojo;
import com.gerp.usermgmt.pojo.transfer.TransferResponsePojo;

import java.util.List;

public interface TransferRequestService {
    Long addTransferRequest(TransferRequestPojo transferRequestPojo);

    List<TransferResponsePojo> getTransferRequest(Long id);

    Page<TransferRequestForOfficePojo> getTransferRequestToOffice(int limit, int page);

    List<TransferResponsePojo> getTransferSelfCreated();

    Long addTransferRequestToTransfer(List<TransferRequestToTransferPojo> transferRequestPojo);

//    TransferResponsePojo getTransferRequestDetailMini(Long id);
}
