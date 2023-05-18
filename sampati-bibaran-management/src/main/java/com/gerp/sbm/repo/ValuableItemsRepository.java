package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.ValuableItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValuableItemsRepository extends JpaRepository<ValuableItems,Long> {
    List<ValuableItems> findAllByPiscode(String piscode);
}
