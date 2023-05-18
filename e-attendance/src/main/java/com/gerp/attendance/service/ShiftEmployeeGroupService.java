package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.model.shift.group.ShiftEmployeeGroup;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.List;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ShiftEmployeeGroupService extends GenericService<ShiftEmployeeGroup, Long> {

    ShiftEmployeeGroup create(ShiftEmployeeGroupPojo data);

    ShiftEmployeeGroup update(ShiftEmployeeGroupPojo data);

    ShiftEmployeeGroupPojo findByIdCustom(Long id);

    List<ShiftEmployeeGroupPojo> getAllCustomEntity(Long fiscalYear);

    Page<ShiftEmployeeGroupPojo> getAllByFiscalYear(GetRowsRequest paginatedRequest);

    List<ShiftEmployeeGroupPojo> getAllCustom();

    List<String> getUsedPisCodeForOffice();
}
