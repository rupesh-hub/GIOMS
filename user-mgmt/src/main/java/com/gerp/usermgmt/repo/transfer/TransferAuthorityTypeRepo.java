package com.gerp.usermgmt.repo.transfer;

import com.gerp.usermgmt.model.transfer.TransferAuthorityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransferAuthorityTypeRepo extends JpaRepository<TransferAuthorityType,Integer> {
    @Transactional
    @Modifying
    @Query("delete from TransferAuthorityType where id=?1")
    void deleteTypeById( int id);
}
