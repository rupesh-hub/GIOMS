package com.gerp.usermgmt.converter.organiztion.orgtransfer;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.employee.EmployeeServiceStatus;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.pojo.organization.employee.FunctionalDesignationPojo;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PositionConverter extends AbstractConverter<PositionPojo, Position> {


    @Autowired
    ModelMapper modelMapper;

    @Override
    public Position toEntity(PositionPojo dto) {
        Position position = new Position();
        modelMapper.map(dto, position);
        return position;
    }

    @Override
    public Position toEntity(PositionPojo dto, Position entity) {
        return super.toEntity(dto, entity);
    }

    @Override
    public PositionPojo toDto(Position entity) {
       try {
           PositionPojo positionPojo = new PositionPojo();
           if(entity != null && entity.getParent() != null) {
               positionPojo.setParentCode(entity.getParent().getCode());
           }
           modelMapper.map(entity, positionPojo);
           return positionPojo;
       } catch (Exception ex){
           ex.printStackTrace();
           return null;
       }

    }

    @Override
    public List<Position> toEntity(List<PositionPojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<PositionPojo> toDto(List<Position> entityList) {
        return super.toDto(entityList);
    }

    public Position toUpdateEntity(PositionPojo dto, Position position) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, position);

        return position;
    }


}
