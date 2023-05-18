package com.gerp.attendance.Converter;

import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.model.shift.Shift;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class ShiftConverter {


    public Shift toEntity(ShiftPojo dto) {
        Shift entity = new Shift();
        return toEntity(dto, entity);
    }

    public Shift toEntity(ShiftPojo dto, Shift entity) {
//        entity.setNameEn(dto.getNameEn());
//        entity.setNameNp(dto.getNameNp());

//      entity.setEndDate(UtilityService.convertStringToDate(dto.getEndDate(),"yyyy-MM-dd"));
//      entity.setStartDate(UtilityService.convertStringToDate(dto.getStartDate(),"yyyy-MM-dd"));
        return entity;
    }

    public ShiftPojo toDto(Shift entity) {
        ShiftPojo dto = new ShiftPojo();
        dto.setId(entity.getId());
//        dto.setNameEn(entity.getNameEn());
//        dto.setNameNp(entity.getNameNp());

//        dto.setStartDate(UtilityService.convertDateToString(entity.getStartDate(), "yyyy-MM-dd"));
//        dto.setEndDate(UtilityService.convertDateToString(entity.getEndDate(), "yyyy-MM-dd"));
        return dto;
    }

    public List<ShiftPojo> toDto(List<Shift> entityList) {
        List<ShiftPojo> shiftPojos = new ArrayList<>();
        for (Shift switchlist : entityList) {
            shiftPojos.add(toDto(switchlist));
        }
        return shiftPojos;
    }

}

