package com.gerp.usermgmt.repo.transfer;

import com.gerp.usermgmt.model.transfer.TransferAuthorityOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransferAuthorityOfficesRepo extends JpaRepository<TransferAuthorityOffice,Integer> {
    @Transactional
    @Modifying
    @Query("delete from TransferAuthorityOffice where id=?1")
    void deleteOfficeById( int id);
}
