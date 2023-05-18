package com.gerp.sbm.repo;

import com.gerp.sbm.model.assets.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepo extends JpaRepository<Vehicle,Long> {
    List<Vehicle> findAllByPiscode(String piscode);
}
