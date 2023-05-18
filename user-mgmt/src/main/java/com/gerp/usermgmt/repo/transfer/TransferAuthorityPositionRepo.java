package com.gerp.usermgmt.repo.transfer;

import com.gerp.usermgmt.model.transfer.TransferAuthorityPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransferAuthorityPositionRepo extends JpaRepository<TransferAuthorityPosition,Integer> {
  @Transactional
  @Modifying
  @Query("delete from TransferAuthorityPosition where id=?1")
    void deletePostionById( int id);
}
