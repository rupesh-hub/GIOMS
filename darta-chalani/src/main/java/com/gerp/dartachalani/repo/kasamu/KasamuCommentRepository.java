package com.gerp.dartachalani.repo.kasamu;

import com.gerp.dartachalani.model.kasamu.KasamuComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KasamuCommentRepository extends JpaRepository<KasamuComment, Long> {

    @Query(value = "select * from kasamu_comment where kasamu_state_id = ?1 order by id desc", nativeQuery = true)
    List<KasamuComment> getByKasamuState(Long id);
}
