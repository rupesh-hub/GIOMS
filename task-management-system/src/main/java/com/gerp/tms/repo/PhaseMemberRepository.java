package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.phase.PhaseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseMemberRepository extends JpaRepository<PhaseMember,Long> {
}
