package com.gerp.usermgmt.mapper.organization;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gerp.usermgmt.model.ScreenGroup;
import com.gerp.usermgmt.model.administrative.AdministrativeBody;
import com.gerp.usermgmt.pojo.organization.administrative.AdministrativeBodyPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface AdministrativeBodyMapper{
    
//    @Select("select a.id ,a.office_code, a.pis_code ,a.name_en a.is_active,kt.name_en as kaaj_type_name,kt.id as kaaj_type_id from administrative_body a left join administrative_level kt on a.kaaj_type_id =kt .id")
//    ArrayList<AdministrativeBodyPojo> getAll();
//
//    @Select("select a.id as id , a.code ,a.from_date ,a.to_date,a.kaaj_name_en ,a.recommendor_pis_code ,a.approver_pis_code ,a.is_active,kt.name_en as kaaj_type_name,kt.id as kaaj_type_id from kaaj_request a left join kaaj_type kt on a.kaaj_type_id =kt .id where a.id =#{id}")
//    AdministrativeBodyPojo getById(Integer id);
//
//    @Select("select a.id as id,a.office_code, a.pis_code ,a.from_date ,a.to_date,a.kaaj_name_en ,a.recommendor_pis_code ,a.approver_pis_code ,a.is_active,kt.name_en as kaaj_type_name,kt.id as kaaj_type_id from kaaj_request a left join kaaj_type kt on a.kaaj_type_id =kt .id where a.pis_code =#{pisCode}")
//    ArrayList<AdministrativeBodyPojo> getByPisCode(String pisCode);

    AdministrativeBodyPojo getById(Integer id);

    List<AdministrativeBodyPojo> getAll();
}
