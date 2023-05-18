package com.gerp.usermgmt.mapper.transfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.shared.pojo.DateListPojo;
import com.gerp.usermgmt.pojo.transfer.RawanaDetailsResponsePojo;
import com.gerp.usermgmt.pojo.transfer.TransferSubmissionResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Mapper
public interface TransferMapper {
    List<TransferSubmissionResponsePojo> getTransferToBeDecided(@Param("pisCode") String pisCode,@Param("code") Long code);

   TransferSubmissionResponsePojo getTransferById(@Param("id") Long id, @Param("type")String type);
    @Select("select document_id from transfer_documents where transfer_history_id=#{id} and type = #{saruwa}")
    Long getSaruwaLetterDocumentId(@Param("id") Long id,@Param("saruwa") String saruwa);

    @Select("select code, short_name_np as nameNp, short_name_en as nameEn,parent_code as parentCode from service where code = #{code}")
    ServicePojo getService(@Param("code") String code);

    List<TransferSubmissionResponsePojo> getRawanaList(@Param("officeCode") String officeCode,@Param("status") String status);

    List<RawanaDetailsResponsePojo> getToBeApprovalRawana(@Param("id") Long id);

    Page<TransferSubmissionResponsePojo> getTransferListForTippadi(Page<TransferSubmissionResponsePojo> pagination, @Param("withIn") String withIn, @Param("searchKey") String searchKey, @Param("status") String status,@Param("code") Long userId);

    DateListPojo getDateRange(@Param("month") LocalDate currentDate,@Param("year") int currentFiscalYear);

 List<DateListPojo> getYearDateRange(@Param("year") int currentFiscalYear);
}
