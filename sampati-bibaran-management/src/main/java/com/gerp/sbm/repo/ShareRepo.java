package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareRepo extends JpaRepository<Share,Long> {
    List<Share> findAllByPiscode(String piscode);
}
