package com.gerp.usermgmt.services.delegation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.delegation.TempDelegationPojo;
import com.gerp.usermgmt.pojo.delegation.TempDelegationResponsePojo;

import java.util.List;

public interface DelegationService {
    int addDelegation(TempDelegationPojo tempDelegationPojo);

    int updateDelegation(TempDelegationPojo tempDelegationPojo);

    Page<TempDelegationResponsePojo> getTemporaryDelegation(String searchKey, int limit, int page, Boolean isReassignment);

    Page<TempDelegationResponsePojo> getTemporaryDelegationList(String searchKey, int limit, int page, Boolean isDelegatedSelf, Boolean isReassignment);

   int deletTempDelegation(int id);

    TempDelegationResponsePojo getTemporaryDelegationById(int id);

    Integer activeDelegation(String pisCode);

    List<TempDelegationResponsePojo> getAllDelegation(String pisCode, Boolean isReassignment);
}
