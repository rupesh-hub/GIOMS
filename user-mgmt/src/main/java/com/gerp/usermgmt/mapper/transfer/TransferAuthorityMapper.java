package com.gerp.usermgmt.mapper.transfer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.model.transfer.TransferAuthority;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import com.gerp.usermgmt.pojo.transfer.MergeOfficeListPojo;
import com.gerp.usermgmt.pojo.transfer.TransferAuthorityResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Mapper
public interface TransferAuthorityMapper {
    List<TransferAuthorityResponsePojo> getTransferConfig(@Param("userId") Long userId);

    List<DetailPojo> getTransferAuthorityOffices(@Param("serviceCode") String serviceCode);

    Page<DetailPojo> getEmployeeToBeTransfered(Page<DetailPojo> detailPojoPage, @Param("employeeName") String code, @Param("officeCode") String loginOfficeCode,@Param("employeeOfficeCode") String employeeOfficeCode);
    Page<DetailPojo> getEmployeeToBeTransferedByOffice(Page<DetailPojo> detailPojoPage, @Param("officeCode") String officeCode, @Param("employeeName") String code, @Param("selectedOffice") String selectedOffice,@Param("positionCodes") Set<String> positionCodes,@Param("serviceCodes") Set<String> serviceCodes);


    @Select("select service_code from employee where pis_code =#{pisCode}")
    String getEmployeeServiceCOde(@Param("pisCode") String pisCode);

    Page<DetailPojo> getEmployeeToBeTransferedWithOffice(Page<DetailPojo> detailPojoPage,@Param("employeeName") String employeeName, @Param("officeCode") String officeCode);

    List<DetailPojo> findByOfficeCode(@Param("officeCode") String officeCode);

    Page<DetailPojo> getEmployeeToBeTransferInternalOffices(Page<DetailPojo> detailPojoPage, @Param("serviceCode") Set<String> serviceCode, @Param("position") Set<String> positions, @Param("officeCode") String officeCode,@Param("searchKey") String searchKey);

    Page<OfficePojo> findTransferOffices(Page<OfficePojo> detailPojoPage, @Param("officeCode") String officeCode,@Param("ministryCode") String ministryCode,@Param("districtCode") String districtCode);

    List<DetailPojo> findUpperLevelOffices(@Param("ministryCode") String ministryCode,@Param("officeCode") String officeCode);

    List<DetailPojo> getConfiguredMinistryOffice(@Param("officeCode") String officeCode);

    Page<OfficePojo> getTransferFromOffice(Page<DetailPojo> detailPojoPage, @Param("officeName") String officeName, @Param("officeCode") String officeCode, @Param("districtCode") String districtCode);

    @Select("delete from transfer_authority ta where ta.id = #{id}} ")
    void deleteById(@Param("id") Integer id);

    @Select("select code     as code    ,\n" +
            "             name_en  as  nameEn   ,\n" +
            "            name_np as nameNp   ,\n" +
            "             parent_code as dobAd from office where code = #{officeCode} ")
    DetailPojo findOffice(@Param("officeCode") String officeCode);

    @Select("WITH RECURSIVE servicess AS (\n" +
            "    select code,name_en,parent_code from service\n" +
            "    where code = #{code}\n" +
            "    union\n" +
            "    select  s.code,s.name_en,s.parent_code from service s inner join servicess ss on ss.code = s.parent_code)\n" +
            "select  code, parent_code,name_en from servicess ss")
    List<String> findAllService(@Param("code") String code);


//   List<MergeOfficeListPojo> getSetupOffice();

}
