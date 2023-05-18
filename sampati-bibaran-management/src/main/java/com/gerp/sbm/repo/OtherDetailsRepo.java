package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.OtherDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtherDetailsRepo extends JpaRepository<OtherDetail,Long> {
    List<OtherDetail> findAllByPiscode(String piscode);
}
