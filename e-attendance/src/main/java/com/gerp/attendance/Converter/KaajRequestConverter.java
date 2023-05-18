package com.gerp.attendance.Converter;

import com.gerp.attendance.Pojo.KaajRequestPojo;
import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.repo.DocumentDetailsRepo;
import com.gerp.attendance.repo.KaajRequestRepo;
import com.gerp.attendance.repo.KaajTypeRepo;
import com.gerp.attendance.service.VehicleCategoryService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class KaajRequestConverter {

    private final KaajRequestRepo kaajRequestRepo;
    private final DocumentDetailsRepo documentDetailsRepo;
    private final VehicleCategoryService vehicleCategoryService;
    private final KaajTypeRepo kaajTypeRepo;
    @Autowired private CustomMessageSource customMessageSource;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private DateConverter dateConverter;

    public KaajRequestConverter(KaajRequestRepo kaajRequestRepo,
//                                CountryRepo countryRepo, ProvinceRepo provinceRepo, DistrictRepo districtRepo, LocationRepo locationRepo,
                                DocumentDetailsRepo documentDetailsRepo, KaajTypeRepo kaajTypeRepo, VehicleCategoryService vehicleCategoryService) {
        this.kaajRequestRepo = kaajRequestRepo;
        this.documentDetailsRepo = documentDetailsRepo;
        this.kaajTypeRepo = kaajTypeRepo;
        this.vehicleCategoryService = vehicleCategoryService;
    }

    public KaajRequest toEntity(KaajRequestPojo dto) {
        KaajRequest entity = new KaajRequest();
        return toEntity(dto, entity);
    }

    public KaajRequest toEntity(KaajRequestPojo dto, KaajRequest entity) {
        if (dto.getVehicleCategoryIds() != null) {
        entity.getVehicleCategories().clear();
        entity.getVehicleCategories().addAll(
                dto.getVehicleCategoryIds().stream().map(x -> vehicleCategoryService.findById(x)).collect(Collectors.toList())
        );
    }
        if(dto.getAppliedForOthers()){
            if(dto.getAppliedPisCode()!=null){
                entity.setAppliedPisCode(tokenProcessorService.getPisCode());
            }
            entity.setPisCode(null);
            entity.setAppliedForOthers(true);
        }else {
            entity.setPisCode(dto.getPisCode());
            entity.setAppliedPisCode(null);
            entity.setAppliedForOthers(false);
            if(dto.getVehicleCategoryIds()==null || dto.getVehicleCategoryIds().isEmpty())
                throw new RuntimeException(customMessageSource.get("empty.array", customMessageSource.get("vehicle.category")));
            entity.getVehicleCategories().clear();
            entity.getVehicleCategories().addAll(
                    dto.getVehicleCategoryIds().stream().map(x->vehicleCategoryService.findById(x)).collect(Collectors.toList())
            );

        }

        if(!dto.getAppliedForOthers()) {
            entity.setToDateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dto.getToDateNp())));
            entity.setToDateNp(dto.getToDateNp());
            entity.setFromDateEn(dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(dto.getFromDateNp())));
            entity.setFromDateNp(dto.getFromDateNp());
            if(dto.getFromDateEn().isEqual(dto.getToDateEn()))
                entity.setDurationType(dto.getDurationType());
        }
        entity.setContent(dto.getKaajRequesterContent());
        entity.setKaajRequesterHashContent(dto.getKaajRequesterHashContent());
        entity.setKaajRequesterSignature(dto.getKaajRequesterSignature());
        entity.setPurpose(dto.getPurpose()==null?null:dto.getPurpose());
        entity.setOfficeCode(tokenProcessorService.getOfficeCode());
        entity.setKaajType(dto.getKaajTypeId() == null ? null : kaajTypeRepo.findById(dto.getKaajTypeId()).get());
        entity.setIsInternational(dto.getIsInternational());
        entity.setIsApproved(dto.getIsApproved());
        entity.setFiscalYear(dto.getFiscalYear());
        entity.setLocation(dto.getLocation()==null?null:dto.getLocation());
        entity.setAdvanceAmountTravel(dto.getAdvanceAmountTravel()==null?null:dto.getAdvanceAmountTravel());
        entity.setRemarkRegardingTravel(dto.getRemarkRegardingTravel()==null?null:dto.getRemarkRegardingTravel());
        if(dto.getIsInternational()!=null && dto.getIsInternational())
            entity.setCountryId(dto.getCountryId());

        entity.setId(dto.getId());
        return entity;
    }

}
