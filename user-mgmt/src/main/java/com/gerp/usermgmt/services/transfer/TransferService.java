package com.gerp.usermgmt.services.transfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.*;

import java.util.List;
import java.util.Set;

public interface TransferService {
    Long addTransfer(List<TransferPojo> transferPojo);

    List<TransferSubmissionResponsePojo> getTransferToBeDecided(Boolean isForEmployee);

    Long updateTransferDecision( TransferDecisionPojo transferDecisionPojo);

    List<TransferSubmissionResponsePojo> getTransferBy(Long id, String type);

    String updateJoiningDate(JoiningDatePojo joiningDatePojo);

    Integer addTransferConfig(List<TransferConfigPojo> transferPojo);

    Long requestRawana(RawanaDetailsPojo rawanaDetailsPojo);

    List<TransferConfigPojo> getTransferConfig();

    OfficePojo getSaruwaMinistry();

    List<TransferSubmissionResponsePojo> getRawanaList(String status);

    Long approveRawana(Long id, boolean approved);

    List<RawanaDetailsResponsePojo> getApproveRawana(Long id);

    Page<TransferSubmissionResponsePojo> getTransferListForTippadi(Boolean withIn, String searchKey, int limit, int page, String status);

    Long addToTippadi(Set<Long> transferIds);

    void deleteTransfer(Long id);

    long updateTransfer(TransferPojo transferRequest);

}
