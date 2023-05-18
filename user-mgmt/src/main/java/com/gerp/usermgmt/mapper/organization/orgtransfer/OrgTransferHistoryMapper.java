package com.gerp.usermgmt.mapper.organization.orgtransfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferHistoryPojo;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrgTransferHistoryMapper {
    List<OrgTransferHistoryPojo> transferHistoryByPisCode(@Param("pisCode") String pisCode);
}
