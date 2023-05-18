package com.gerp.attendance.service;


import com.gerp.attendance.Pojo.VehicleCategoryPojo;
import com.gerp.attendance.model.kaaj.VehicleCategory;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface VehicleCategoryService extends GenericService<VehicleCategory, Integer> {

    ArrayList<VehicleCategoryPojo> getAllVehicle();

    void deleteVehicle(Integer vehicleId);

}
