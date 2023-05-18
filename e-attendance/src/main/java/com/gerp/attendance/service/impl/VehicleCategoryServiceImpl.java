package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.VehicleCategoryPojo;
import com.gerp.attendance.mapper.VehicleCategoryMapper;
import com.gerp.attendance.model.kaaj.VehicleCategory;
import com.gerp.attendance.repo.VehicleCategoryRepo;
import com.gerp.attendance.service.VehicleCategoryService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class VehicleCategoryServiceImpl extends GenericServiceImpl<VehicleCategory, Integer> implements VehicleCategoryService {

    private final VehicleCategoryRepo vehicleCategoryRepo;
    private final VehicleCategoryMapper vehicleCategoryMapper;
    private final CustomMessageSource customMessageSource;

    public VehicleCategoryServiceImpl(VehicleCategoryRepo vehicleCategoryRepo, VehicleCategoryMapper vehicleCategoryMapper, CustomMessageSource customMessageSource) {
        super(vehicleCategoryRepo);
        this.vehicleCategoryRepo = vehicleCategoryRepo;
        this.vehicleCategoryMapper=vehicleCategoryMapper;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public VehicleCategory findById(Integer uuid) {
        VehicleCategory vehicleCategory = super.findById(uuid);
        if (vehicleCategory == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("vehicle.category")));
        return vehicleCategory;
    }

    @Override
    public ArrayList<VehicleCategoryPojo> getAllVehicle() {
        return vehicleCategoryMapper.getAllVehicleCategory();
    }

    @Override
    public void deleteVehicle(Integer vehicleId) {
       vehicleCategoryRepo.deleteById(vehicleId);
    }
}
