package com.gerp.usermgmt.repo;

import com.gerp.usermgmt.model.IndividualScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IndividualScreenRepo extends JpaRepository<IndividualScreen, Long> {
    Optional<IndividualScreen> findByKey(String key);

    @Query(value = "select * from individual_screen where screen_group_id=?1", nativeQuery = true)
    List<IndividualScreen> findAllByScreenGroup(Long screenGroupId);
}
