package com.gerp.usermgmt.mapper.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.pojo.OfficeGroupDto;
import com.gerp.usermgmt.pojo.external.TMSOfficePojo;
import com.gerp.usermgmt.pojo.organization.office.ExternalOfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeGroupPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface OfficeMapper {

    List<OfficePojo> getAllParentOffice(@Param("orgTypeId") Long orgTypeId);

    @Select("WITH recursive q AS (select o.parent_code, o.code\n" +
            "                                         from office o\n" +
            "                                         where o.code = #{code}\n" +
            "                                         UNION ALL\n" +
            "                                         select z.parent_code, z.code\n" +
            "                                         from office z\n" +
            "                                                  join q on z.code = q.parent_code\n" +
            "                    )\n" +
            "                    SELECT q.code from q where q.code != #{code}")
    List<String> getAllParentOfficeIds(@Param("code") String code);

    @Select("WITH recursive q AS (select code, parent_code,is_gioms_active\n" +
            "                      from office o\n" +
            "                      where o.code = '00'\n" +
            "                      UNION ALL\n" +
            "                      select z.code, z.parent_code,z.is_gioms_active\n" +
            "                      from office z\n" +
            "                               join q on z.parent_code = q.code\n" +
            "\n" +
            "\n" +
            ") select code from q\n" +
            " where q.is_gioms_active=true")
    List<String> getLowerOffice(String officeCode);


    String getTopLevelOffice(@Param("code") String code, @Param("excludeOffices") List<String> excludeOffices);

    Page<OfficePojo> getALlOffices(Page<OfficePojo> officePojoPage, @Param("searchKey") String searchKey,@Param("district") String district);

    List<OfficePojo> getALlOfficesByIds(@Param("ids") List<String> ids);

    OfficePojo getOfficeSectionByParent(String officeCode);

    List<OfficePojo> getChildOffices(String officeCode);

    List<String> getEmployeeList(@Param("offices") List<String> offices);

    List<String> getAllChildOfficeCode(String officeCode);

    OfficePojo getOfficeByCode(String officeCode);

    List<OfficePojo> getOfficeLowerHierarchy(String officeCode);

    List<OfficePojo> getAllOfficeListByParam();

    List<OfficePojo> getOfficeByFilter(@Param("map") Map<String, Object> map);

    List<OfficePojo> getGiomsActiveOffice();

    List<OfficePojo> getChildOffice(String officeCode);
    List<OfficePojo> getParentOffice(String officeCode);

    List<OfficePojo> getMinistryOffices(@Param("ids") List<String> ids);

    @Select("select parent_code from office where code = #{officeCode} ")
    String getParentOfficeCode(String officeCode);

    List<OfficeGroupPojo> getOfficeGroup(@Param("pisCode") Long pisCode, @Param("officeCode") String officeCode, @Param("districtCode") String districtCode, @Param("officeLevelCode") String officeLevelCode,@Param("ownOfficeCode") String code);
    OfficeGroupDto getOfficeGroupById(@Param("id") Integer id);
    @Select(value = "select *,office_code as code from office_group where office_code = #{officeCode} and (type = #{type} or type='others')")
    List<ExternalOfficePojo> findByType(@Param("type") String type,@Param("officeCode") String officeCode);

    Page<OfficePojo> searchOfficePaginated( Page<OfficePojo> page,  @Param("searchField") Map<String, Object> searchField, @Param("orgTypeId") Long orgTypeId);


    OfficePojo getOfficeDetailMinimal(String officeCode);

    @Select("select d.code,d.name_en as name,d.name_np as nameN from office o inner join district d on o.district_code = d.code\n" +
            "where o.code = #{officeCode}  ")
    IdNamePojo getOfficeDistrict(@Param("officeCode") String officeCode);

    List<Object> getAllOfficeByGIOMSStatus(@Param("isActive") boolean isActive);


    List<TMSOfficePojo> getAllOfficeByCodes(@Param("officeCodes") List<String> officeCodes);
}
