//package com.gerp.attendance.Converter;
//
//import com.gerp.attendance.Pojo.LocationPojo;
//import com.gerp.attendance.model.setup.Location;
//import com.gerp.attendance.repo.DistrictRepo;
//import org.springframework.stereotype.Component;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@Component
//public class LocationConverter {
//    private final DistrictRepo districtRepo;
//
//
//    public LocationConverter(DistrictRepo districtRepo) {
//        this.districtRepo = districtRepo;
//
//    }
//
//    public Location toEntity(LocationPojo dto) {
//        Location entity = new Location();
//        return toEntity(dto, entity);
//    }
//
//    public Location toEntity(LocationPojo dto, Location entity) {
//        entity.setNameEn(dto.getNameEn());
//        entity.setNameNp(dto.getNameNp());
//        entity.setDistrictId(dto.getDistrictId() == null ? null : districtRepo.findById(dto.getDistrictId()).get());
//        entity.setId(dto.getId());
//        return entity;
//    }
//}
