package com.gerp.usermgmt.mapper.organization.orgtransfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface OrgTransferRequestMapper {
    Page<OrgTransferRequestPojo> searchCurrentOfficeTransfersPaginated(
            Page<OrgTransferRequestPojo> page,
            @Param("searchField") Map<String, Object> searchField, @Param("officeCode") String officeCode);

    Page<OrgTransferRequestPojo> searchAllOfficeTransfersPaginated(
            Page<OrgTransferRequestPojo> page,
            @Param("searchField") Map<String, Object> searchField);

    OrgTransferRequestPojo transferDetail(@Param("id") Long id);

    OrgTransferRequestPojo pendingTransferOfEmployee(@Param("pisCode") String pisCode);


}
