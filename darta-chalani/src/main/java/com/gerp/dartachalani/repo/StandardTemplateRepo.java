package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.StandardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardTemplateRepo extends JpaRepository<StandardTemplate,Integer> {
}
