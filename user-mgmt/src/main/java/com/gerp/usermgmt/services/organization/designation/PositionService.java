package com.gerp.usermgmt.services.organization.designation;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;

import java.util.List;


public interface PositionService extends GenericService<Position, String> {

    List<IdNamePojo> positions();

    List<IdNamePojo> topParentPosition();

    List<String> getAllNodePositionCode(String positionCode, Long orderNo);
    List<String> getAllNodePositionCodeWithSelf(String positionCode, Long orderNo);
    List<String> getAllNodePositionCodeWithOutSelf(String positionCode, Long orderNo);
    List<String> getAllLowerNodePositionCodeWithSelf(String positionCode, Long orderNo);

    PositionPojo positionDetailByPis(String pisCode);

    List<IdNamePojo> getOfficePositions();

    String save(PositionPojo position);

    String update(PositionPojo position);

    List<PositionPojo> positionSearch(SearchPojo searchPojo);
}
