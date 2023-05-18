package com.gerp.usermgmt.mapper.transfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.transfer.TransferRequestForOfficePojo;
import com.gerp.usermgmt.pojo.transfer.TransferResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Mapper
public interface TransferRequestMapper {
   List<TransferResponsePojo> getTransferRequest(@Param("id") Long id);
    List<TransferResponsePojo> getTransferCreatedBySelf(@Param("employeePsCode") String employeePsCode);

    Page<TransferRequestForOfficePojo> getTransferRequestToOffice(Page<TransferRequestForOfficePojo> transferRequestForOfficePojoPage, @Param("officeCode") String officeCode);

    @Select("select document_id from transfer_request_document where transfer_request_id=#{id} and type = #{saruwa}")
    Long getDocumentId(@Param("id") Long id,@Param("saruwa") String saruwa);

}
