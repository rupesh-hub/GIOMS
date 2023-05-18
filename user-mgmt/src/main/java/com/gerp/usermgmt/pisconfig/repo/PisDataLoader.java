//package com.gerp.usermgmt.pisconfig.repo;
//
//import com.gerp.usermgmt.pisconfig.model.DemoDistrict;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//import java.util.Map;
//
//public interface PisDataLoader extends JpaRepository<DemoDistrict, String> {
//
//    @Query(nativeQuery = true, value = "select *\n" +
//			"from\n" +
//			"    ( select\n" +
//			"          e.EMPLOYEE_CD as pis_code,\n" +
//			"          e.APP_SERVICE_CD as app_service_code,\n" +
//			"          e.APP_POSITION_CD as app_position_code,\n" +
//			"\n" +
//			"          e.APP_DESIGNATION_CD AS app_designation_code,\n" +
//			"          e.APP_SERVICE_TYPE_CD as app_service_type_code,\n" +
//			"          e.APP_SERVICE_STATUS_CD as app_service_status_code,\n" +
//			"\n" +
//			"          e.FIRST_NAME_ENG as first_name_en,\n" +
//			"          e.FIRST_NAME_LOC as first_name_np,\n" +
//			"          e.MIDDLE_NAME_ENG as middle_name_en,\n" +
//			"          e.MIDDLE_NAME_LOC as middle_name_np,\n" +
//			"             e.LAST_NAME_ENG as last_name_en,\n" +
//			"             e.LAST_NAME_LOC as last_name_np,\n" +
//			"          e.FATHER_FNAME_ENG as father_fname_en,\n" +
//			"          e.FATHER_FNAME_LOC as father_fname_np,\n" +
//			"          e.FATHER_MNAME_ENG as father_mname_en,\n" +
//			"          e.FATHER_MNAME_LOC as father_mname_np,\n" +
//			"          e.FATHER_LNAME_ENG as father_lname_en,\n" +
//			"          e.FATHER_LNAME_LOC as father_lname_np,\n" +
//			"          e.FATHER_FULL_NAME_ENG as father_full_name_en,\n" +
//			"          e.FATHER_FULLNAME_LOC as father_full_name_np,\n" +
//			"          e.MOTHER_FNAME_ENG as mother_fname_en,\n" +
//			"          e.MOTHER_FNAME_LOC as mother_fname_np,\n" +
//			"          e.MOTHER_MNAME_ENG as mother_mname_en,\n" +
//			"          e.MOTHER_MNAME_LOC AS mother_mname_np,\n" +
//			"          e.MOTHER_LNAME_ENG as mother_lname_en,\n" +
//			"          e.MOTHER_LNAME_LOC as mother_lname_np,\n" +
//			"          e.MOTHER_FULL_NAME_ENG as mother_full_name_en,\n" +
//			"          e.MOTHER_FULLNAME_LOC as mother_full_name_np,\n" +
//			"          e.GFATHER_FNAME_ENG as grand_father_fname_en,\n" +
//			"          e.GFATHER_FNAME_LOC as grand_father_fname_np,\n" +
//			"          e.GFATHER_MNAME_ENG as grand_father_mname_en,\n" +
//			"          e.GFATHER_MNAME_LOC as grand_father_mname_np,\n" +
//			"          e.GFATHER_LNAME_ENG as grand_father_lname_en,\n" +
//			"          e.GFATHER_LNAME_LOC as grand_father_lname_np,\n" +
//			"          e.GFATHER_FULLNAME_LOC as grand_father_full_name_np,\n" +
//			"          e.GFATHER_FULL_NAME_ENG as grand_father_full_name_en,\n" +
//			"          e.SPOUSE_FNAME_ENG as spouse_fname_en,\n" +
//			"          e.SPOUSE_FNAME_LOC as spouse_fname_np,\n" +
//			"          e.SPOUSE_MNAME_ENG as spouse_mname_en,\n" +
//			"          e.SPOUSE_MNAME_LOC as spouse_mname_np,\n" +
//			"          e.SPOUSE_LNAME_ENG as spouse_lname_en,\n" +
//			"          e.SPOUSE_LNAME_LOC as spouse_lname_np,\n" +
//			"          e.SPOUSE_FULL_NAME_ENG as spouse_full_name_en,\n" +
//			"          e.SPOUSE_FULLNAME_LOC as spouse_full_name_np,\n" +
//			"\n" +
//			"          e.PER_DISTRICT_CD as per_district_code,\n" +
//			"          e.MARITAL_STATUS_CD as marital_status,\n" +
//			"          e.BLOOD_GROUP_CD as blood_group,\n" +
//			"          e.CUR_OFFICE_CD as office_code,\n" +
//			"          e.App_OFFICE_CD as app_office_code,\n" +
//			"\n" +
//			"          e.BIRTH_DT as birth_date_ad,\n" +
//			"          e.BIRTH_DT_LOC as birth_date_bs,\n" +
//			"          e.APPROVED_DT as approved_date_en,\n" +
//			"\n" +
//			"          e.CUR_POSITION_CD as position_code,\n" +
//			"          e.CUR_POSITION_APP_DT as current_position_app_date_ad,\n" +
//			"          e.CUR_POSITION_APP_DT_LOC as current_position_app_date_bs,\n" +
//			"          e.CUR_SERVICE_CD as service_code,\n" +
//			"          e.CUR_DESIGNATION_CD as designation_code,\n" +
//			"          e.RELIGION_CD as religion_code,\n" +
//			"          e.TMP_DISTRICT_CD as temp_district_code,\n" +
//			"          e.TMP_VDC_MUN_CD as temp_municipality_vdc,\n" +
//			"          e.CITIZENSHIP_NO as citizenship_number,\n" +
//			"          e.REF_EMPLOYEE_CD as ref_emp_code,\n" +
//			"          e.PER_VDC_MUN_CD as per_municipality_vdc,\n" +
//			"          e.GENDER as gender,\n" +
//			"          e.PAN_NO as pan,\n" +
//			"          e.APPROVED AS approved,\n" +
//			"          e.APPROVED_DT_LOC as approved_date_np,\n" +
//			"          e.MOBILE_NUMBER as mobile_number,\n" +
//			"          e.CUR_OFFICE_JOIN_DT_LOC as cur_office_join_dt_np,\n" +
//			"          e.CUR_OFFICE_JOIN_DT as cur_office_join_dt_en,\n" +
//			"          e.EMAIL as email_address,\n" +
//			"             ROWNUM r\n" +
//			"      from ENDVHR.HR_EMPLOYEE e\n)" +
//			"where r>= ?1\n" +
//			"  and r <= ?2")
//    List<Map<String, Object>> findEmployee(int skip , int size);
//
//    @Query(value = "select count(*) from ENDVHR.HR_EMPLOYEE", nativeQuery = true)
//    Long countEmployee();
//
//
//    @Query(value = "select\n" +
//			"            hd.RELIGION_CD as code,\n" +
//			"            hd.DEFINED_CD as defined_code,\n" +
//			"            hd.DESC_ENG as name_en,\n" +
//			"            hd.DESC_LOC as name_np,\n" +
//			"            hd.DISABLED as is_active,\n" +
//			"            hd.APPROVED as approved,\n" +
//			"            hd.APPROVED_BY as approved_by,\n" +
//			"            hd.APPROVED_DT as approved_date_en,\n" +
//			"            hd.APPROVED_DT_LOC as approved_date_np,\n" +
//			"            ENTERED_DT as created_date FROM ENDVHR.HR_RELIGION hd", nativeQuery = true)
//    List<Map<String, Object>> findReligion();
//
//    @Query(value = "\n" +
//            "select  \n" +
//            "hd.DISTRICT_CD as code, \n" +
//            "hd.DEFINED_CD as defined_code, \n" +
//            "hd.DESC_ENG as name_en, \n" +
//            "hd.DESC_LOC as name_np, \n" +
//            "hd.DISABLED as is_active, \n" +
//            "hd.APPROVED as approved, \n" +
//            "hd.APPROVED_BY as approved_by, \n" +
//            "hd.APPROVED_DT as approved_date_en, \n" +
//            "hd.APPROVED_DT_LOC as approved_date_np, \n" +
//            "ENTERED_DT as created_date FROM ENDVHR.HR_DISTRICT hd", nativeQuery = true)
//    List<Map<String, Object>> findDistrict();
//
//    @Query(value = "Select \n" +
//            "hvm.DEFINED_CD as code, \n" +
//            "hvm.DESC_ENG as name_en, \n" +
//            "hvm.DESC_LOC as name_np, \n" +
//            "hvm.DISABLED as is_active, \n" +
//            "hvm.APPROVED as approved, \n" +
//            "hvm.APPROVED_BY as approved_by, \n" +
//            "hvm.APPROVED_DT as approved_date_en, \n" +
//            "hvm.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hvm.ENTERED_BY as entered_by, \n" +
//            "hvm.ENTERED_DT as created_date FROM ENDVHR.HR_VDC_MUNICIPALITY hvm", nativeQuery = true)
//    List<Map<String, Object>> findMunicipality();
//
//    @Query(value = "   select\n" +
//			"            hd.COUNTRY_CD as code,\n" +
//			"            hd.DEFINED_CD as defined_code,\n" +
//			"            hd.DESC_ENG as name_en,\n" +
//			"            hd.DESC_LOC as name_np,\n" +
//			"            hd.DISABLED as is_active,\n" +
//			"            hd.APPROVED as approved,\n" +
//			"            hd.APPROVED_BY as approved_by,\n" +
//			"            hd.APPROVED_DT as approved_date_en,\n" +
//			"            hd.APPROVED_DT_LOC as approved_date_np,\n" +
//			"            ENTERED_DT as created_date FROM ENDVHR.HR_COUNTRY hd", nativeQuery = true)
//    List<Map<String, Object>> findCountry();
//
//    @Query(value = "Select \n" +
//            "hoc.OFFICE_CAT_CD as code, \n" +
//            "hoc.DEFINED_CD as defined_code, \n" +
//            "hoc.DESC_ENG as name_en, \n" +
//            "hoc.DESC_LOC as name_np, \n" +
//            "hoc.SHORT_NAME as short_name_en, \n" +
//            "hoc.SHORT_NAME_LOC as short_name_np, \n" +
//            "hoc.ORDER_NO as order_no, \n" +
//            "hoc.DISABLED as is_active, \n" +
//            "hoc.APPROVED as approved, \n" +
//            "hoc.APPROVED_BY as approved_by, \n" +
//            "hoc.APPROVED_DT as approved_date_en, \n" +
//            "hoc.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hoc.ENTERED_BY as entered_by, \n" +
//            "hoc.ENTERED_DT as created_date FROM ENDVHR.HR_OFFICE_CATEGORY hoc ", nativeQuery = true)
//    List<Map<String, Object>> findOfficeCategory();
//
//    @Query(value = "Select \n" +
//            "hol.OFFICE_LEVEL_CD as code, \n" +
//            "hol.DEFINED_CD as defined_code, \n" +
//            "hol.DESC_ENG as name_en, \n" +
//            "hol.DESC_LOC as name_np, \n" +
//            "hol.SHORT_NAME as short_name_en, \n" +
//            "hol.SHORT_NAME_LOC as short_name_np, \n" +
//            "hol.HOD_TITLE as hod_title_en, \n" +
//            "hol.HOD_TITLE_LOC as hod_titile_np, \n" +
//            "hol.LEVEL_ORDER_NO as order_no, \n" +
//            "hol.DISABLED as is_active, \n" +
//            "hol.APPROVED as approved, \n" +
//            "hol.APPROVED_BY as approved_by, \n" +
//            "hol.APPROVED_DT as approved_date, \n" +
//            "hol.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hol.ENTERED_BY as entered_by, \n" +
//            "hol.ENTERED_DT as created_date FROM ENDVHR.HR_OFFICE_LEVEL hol ", nativeQuery = true)
//    List<Map<String, Object>> findOrganizationLevel();
//
//    @Query(value = "Select \n" +
//            "hd.DESIGNATION_CD as code, \n" +
//            "hd.DEFINED_CD as defined_code, \n" +
//            "hd.DESC_ENG as name_en, \n" +
//            "hd.DESC_LOC as name_np, \n" +
//            "hd.SHORT_NAME as short_name_en, \n" +
//            "hd.SHORT_NAME_LOC as short_name_np, \n" +
//            "hd.HAS_SUB_CLASS as has_sub_class, \n" +
//            "hd.DISABLED as is_active, \n" +
//            "hd.ORDER_NO as order_no, \n" +
//            "hd.APPROVED as approved, \n" +
//            "hd.APPROVED_BY as approved_by, \n" +
//            "hd.APPROVED_DT as approved_date_en, \n" +
//            "hd.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hd.ENTERED_BY as entered_by, \n" +
//            "hd.ENTERED_DT as created_date FROM ENDVHR.HR_DESIGNATION hd", nativeQuery = true)
//    List<Map<String, Object>> findDesignation();
//
//    @Query(value = "Select \n" +
//            "hp.POSITION_CD as code, \n" +
//            "hp.DEFINED_CD as defined_code, \n" +
//            "hp.DESC_ENG as name_en, \n" +
//            "hp.DESC_LOC as name_np, \n" +
//            "hp.SHORT_NAME as short_name_en, \n" +
//            "hp.SHORT_NAME_LOC as short_name_np ,\n" +
//            "hp.UPPER_POSITION_CD as parent_position_code, \n" +
//            "hp.ORDER_NO as order_no, \n" +
//            "hp.SOURCE_OF_INSERTION as source, \n" +
//            "hp.DISABLED as is_active, \n" +
//            "hp.APPROVED as approved, \n" +
//            "hp.APPROVED_BY as approved_by, \n" +
//            "hp.APPROVED_DT as approved_date_en, \n" +
//            "hp.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hp.ENTERED_BY as entered_by, \n" +
//            "hp.CREATION_DT as created_date FROM ENDVHR.HR_POSITION hp  ", nativeQuery = true)
//    List<Map<String, Object>> findPosition();
//
//    @Query(value = "Select \n" +
//            "hss.SERVICE_STATUS_CD as code, \n" +
//            "hss.DEFINED_CD as defined_code, \n" +
//            "hss.DESC_ENG as name_en, \n" +
//            "hss.DESC_LOC as name_np, \n" +
//            "hss.SHORT_NAME as short_name_en, \n" +
//            "hss.SHORT_NAME_LOC as short_name_np ,\n" +
//            "hss.DISABLED as is_active, \n" +
//            "hss.ORDER_NO as order_no, \n" +
//            "hss.APPROVED as approved, \n" +
//            "hss.APPROVED_BY as approved_by, \n" +
//            "hss.APPROVED_DT as approved_date_en, \n" +
//            "hss.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hss.ENTERED_BY as entered_by, \n" +
//            "hss.ENTERED_DT as created_date FROM ENDVHR.HR_SERVICE_STATUS hss ", nativeQuery = true)
//    List<Map<String, Object>> findServiceStatus();
//
//    @Query(value = "Select \n" +
//            "hst.SERVICE_TYPE_CD as code, \n" +
//            "hst.DEFINED_CD as defined_code, \n" +
//            "hst.DESC_ENG as name_en, \n" +
//            "hst.DESC_LOC as name_np,\n" +
//            "hst.SHORT_NAME as short_name_en, \n" +
//            "hst.SHORT_NAME_LOC as short_name_np, \n" +
//            "hst.DISABLED as is_active, \n" +
//            "hst.ORDER_NO as order_no, \n" +
//            "hst.APPROVED as approved, \n" +
//            "hst.APPROVED_BY as approvd_by, \n" +
//            "hst.APPROVED_DT as approved_date_en, \n" +
//            "hst.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hst.ENTERED_BY as entered_by,\n" +
//            "hst.ENTERED_DT as created_DATE FROM ENDVHR.HR_SERVICE_TYPE hst", nativeQuery = true)
//    List<Map<String, Object>> findServiceType();
//
//    @Query(value = "SELECT \n" +
//            "hs.SERVICE_CD as code, \n" +
//            "hs.UPPER_SERVICE_CD as parent_code, \n" +
//            "hs.SERVICE_TYPE_CD as service_type_code, \n" +
//            "hs.DEFINED_CD as defined_code, \n" +
//            "hs.DESC_ENG as name_en, \n" +
//            "hs.DESC_LOC as name_np, \n" +
//            "hs.SHORT_NAME as short_name_en, \n" +
//            "hs.SHORT_NAME_LOC as short_name_np, \n" +
//            "ORDER_NO as order_no, \n" +
//            "hs.DISABLED as is_active ,\n" +
//            "hs.APPROVED as approved, \n" +
//            "hs.APPROVED_BY as approved_by, \n" +
//            "hs.APPROVED_DT as approved_date_en, \n" +
//            "hs.APPROVED_DT_LOC as approved_date_np, \n" +
//            "hs.ENTERED_BY as entered_by, \n" +
//            "hs.ENTERED_DT as created_date FROM ENDVHR.HR_SERVICE hs", nativeQuery = true)
//    List<Map<String, Object>> findService();
//
//    @Query(value = "Select \n" +
//			"ho.VDC_MUN_CD as municipality_vdc_code, \n" +
//			"ho.UPPER_OFFICE_CD as parent_code, \n" +
//			"ho.URL as url, \n" +
//			"ho.TELEPHONE_NO as telephone_no, \n" +
//			"ho.REMARKS_LOC as remarks_np, \n" +
//			"ho.REMARKS as remarks, \n" +
//			"ho.OFFICE_SUFFIX_LOC as office_suffix_np, \n" +
//			"ho.OFFICE_SUFFIX as office_suffix_en, \n" +
//			"ho.OFFICE_SERIAL_NO as office_serial_no, \n" +
//			"ho.OFFICE_PREFIX_LOC as office_prefix_np, \n" +
//			"ho.OFFICE_PREFIX as office_prefix_en ,\n" +
//			"ho.OFFICE_NAME_CD as office_name_code, \n" +
//			"ho.OFFICE_DISSOLVE_DT as office_dissolved_date, \n" +
//			"ho.OFFICE_CD as code, \n" +
//			"ho.OFFICE_CAT_CD as office_category_code, \n" +
//			"ho.FAX_NO as fax_no, \n" +
//			"ho.ENTERED_DT as created_date, \n" +
//			"ho.ENTERED_BY as entered_by, \n" +
//			"ho.EMAIL as email, \n" +
//			"ho.DISTRICT_CD as district_code, \n" +
//			"ho.DISABLED as is_active, \n" +
//			"ho.DESC_LOC as name_np, \n" +
//			"ho.DESC_ENG as name_en, \n" +
//			"ho.DEFINED_CD as defined_code ,\n" +
//			"ho.COUNTRY_CD as country_code, \n" +
//			"ho.APPROVED_DT_LOC as approved_date_np, \n" +
//			"ho.APPROVED_DT as approved_date_en, \n" +
//			"ho.APPROVED_BY as approved_by, \n" +
//			"ho.APPROVED as approved, \n" +
//			"ho.ALLOW_OVERSTAFFING as allow_over_staffing, \n" +
//			"ho.ADDRESS_LOC as address_np, \n" +
//			"ho.OFFICE_CREATE_DT as office_create_dt, \n" +
//			"ho.OFFICE_LEVEL_CD  as organization_level_code , \n" +
//			"ho.ADDRESS_ENG as address_en FROM ENDVHR.HR_OFFICE ho", nativeQuery = true)
//    List<Map<String, Object>> findOffice();
//
//	@Query(value = "SELECT hsp.SERVICE_CD  AS service_code, hsp.POSITION_CD AS position_code, hsp.DESIGNATION_CD AS designation_code \n" +
//			", hsp.APPROVED , hsp.APPROVED_BY, hsp.APPROVED_DT AS approved_date_en, hsp.APPROVED_DT_LOC AS  approved_date_np\n" +
//			"FROM ENDVHR.HR_SERVICE_POSITION hsp", nativeQuery = true)
//	List<Map<String, Object>> findDesignationDetail();
//
//	@Query(value = "select e.EDUCATION_LEVEL_CD as code, e.DESC_ENG as name_en, e.DESC_LOC as name_np,\n" +
//			"       e.SHORT_NAME as short_name_en, e.SHORT_NAME_LOC as short_name_np, (e.DISABLED + 1)%2 as is_active, e.UPPER_EDUCATION_LEVEL_CD as parent_code,\n" +
//			"       e.ORDER_NO as order_no, e.APPROVED as approved, e.APPROVED_BY as approved_by , e.APPROVED_DT as approved_date_en,e.APPROVED_DT_LOC as approved_date_np,\n" +
//			"       e.ENTERED_DT as created_date, e.ENTERED_BY as entered_by\n" +
//			"from ENDVHR.HR_EDU_LEVEL e", nativeQuery = true)
//	List<Map<String, Object>> findEducationLevels();
//
//	@Query(value = "SELECT hsp.SERVICE_CD  AS service_code, hsp.POSITION_CD AS position_code, hsp.DESIGNATION_CD AS designation_code \n" +
//			", hsp.APPROVED , hsp.APPROVED_BY, hsp.APPROVED_DT AS approved_date_en, hsp.APPROVED_DT_LOC AS  approved_date_np\n" +
//			"FROM ENDVHR.HR_SERVICE_POSITION hsp", nativeQuery = true)
//	List<Map<String, Object>> findMunicipalityVdcRelation();
//
//	@Query(value = "select e.FACULTY_CD as code, e.DESC_ENG as name_en, e.DESC_LOC as name_np,\n" +
//			"       e.SHORT_NAME as short_name_en, e.SHORT_NAME_LOC as short_name_np, case when e.DISABLED = 'N' then 1 else 0 end as is_active, e.UPPER_EDUCATION_LEVEL_CD as parent_code,\n" +
//			"       e.ORDER_NO as order_no, e.APPROVED as approved, e.APPROVED_BY as approved_by , e.APPROVED_DT as approved_date_en,e.APPROVED_DT_LOC as approved_date_np,\n" +
//			"       e.ENTERED_DT as created_date, e.ENTERED_BY as entered_by\n" +
//			"from ENDVHR.HR_EDU_LEVEL e", nativeQuery = true)
//	List<Map<String, Object>> findFacultyData();
//}
