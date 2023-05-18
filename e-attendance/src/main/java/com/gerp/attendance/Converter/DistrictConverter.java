//package com.gerp.attendance.Converter;
//
//import com.gerp.attendance.Pojo.DistrictPojo;
//import com.gerp.attendance.model.setup.District;
//import com.gerp.attendance.repo.ProvinceRepo;
//import org.springframework.stereotype.Component;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@Component
//public class DistrictConverter {
//
//    private final ProvinceRepo provinceRepo;
//
//
//    public DistrictConverter(ProvinceRepo provinceRepo) {
//        this.provinceRepo= provinceRepo;
//
//    }
//
//    public District toEntity(DistrictPojo dto) {
//        District entity = new District();
//        return toEntity(dto, entity);
//    }
//
//    public District toEntity(DistrictPojo dto, District entity) {
//        entity.setNameEn(dto.getNameEn());
//        entity.setNameNp(dto.getNameNp());
//        entity.setProvinceId(dto.getProvinceId() == null ? null :provinceRepo.findById(dto.getProvinceId()).get());
//        entity.setId(dto.getId());
//        return entity;
//    }
//}
