package com.gerp.usermgmt.repo.transfer;

import com.gerp.usermgmt.model.transfer.TransferAuthorityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransferAuthorityGroupRepo extends JpaRepository<TransferAuthorityGroup,Integer> {
    @Transactional
    @Modifying
    @Query("delete from TransferAuthorityGroup where id=?1")
    void deleteGroupById( int id);
}
