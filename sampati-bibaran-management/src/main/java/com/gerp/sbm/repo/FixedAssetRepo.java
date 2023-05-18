package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.FixedAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedAssetRepo extends JpaRepository<FixedAsset,Long> {
    List<FixedAsset> findAllByPiscode(String piscode);
}
