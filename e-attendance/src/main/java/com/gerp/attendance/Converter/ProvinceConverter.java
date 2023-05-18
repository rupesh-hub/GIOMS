//package com.gerp.attendance.Converter;
//
//import com.gerp.attendance.Pojo.ProvincePojo;
//import com.gerp.attendance.model.setup.Province;
//import com.gerp.attendance.repo.CountryRepo;
//import org.springframework.stereotype.Component;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@Component
//public class ProvinceConverter {
//
//
//    private final CountryRepo countryRepo;
//
//
//    public ProvinceConverter(CountryRepo countryRepo) {
//        this.countryRepo= countryRepo;
//
//    }
//
//    public Province toEntity(ProvincePojo dto) {
//        Province entity = new Province();
//        return toEntity(dto, entity);
//    }
//
//    public Province toEntity(ProvincePojo dto, Province entity) {
//        entity.setNameEn(dto.getNameEn());
//        entity.setNameNp(dto.getNameNp());
//        entity.setCountryId(dto.getCountryId() == null ? null :countryRepo.findById(dto.getCountryId()).get());
//        entity.setId(dto.getId());
//        return entity;
//    }
//}
