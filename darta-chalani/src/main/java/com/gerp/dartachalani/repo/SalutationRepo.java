package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.dto.SalutationPojo;
import com.gerp.dartachalani.model.Salutation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalutationRepo extends JpaRepository<Salutation,Long> {

    List<Salutation> findByCreator(String pisCode);
}
