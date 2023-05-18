package com.gerp.shared.constants.cacheconstants;

public class OfficeCacheConst {
    public OfficeCacheConst() {throw new IllegalStateException("Utility Class");}
    public static final String CACHE_VALUE_MINIMAL = "officeMinimalDetail";
    public static final String CACHE_VALUE_DETAIL = "officeDetail";
    public static final String CACHE_SET_KEY = "#officeCode";
    public static final String CACHE_EVICT_KEY = "#office.getCode()";
    public   static final String CACHE_UNLESS_CONDITION = "#result == null";
    public static final  String CACHE_CONDITION = "#result.code != null";

    public static final String CACHE_VALUE_FISCAL_YEAR = "fiscalYear";

    public static final String CACHE_KEY_ACTIVE_YEAR = "#result.getCode()";

    public static final String CACHE_VALUE_SECTION = "sectionValue";

    public static final String CACHE_KEY_SECTION = "#sectionId";

    public static final String CACHE_VALUE_DESIGNATION_POJO = "designationPojo";

    public static final String CACHE_KEY_DESIGNATION_POJO = "#designationCode";

    public static final String CACHE_VALUE_DESIGNATION_DETAIL_POJO = "designationDetailPojo";

    public static final String CACHE_KEY_DESIGNATION_DETAIL_POJO = "#code";


    public static final String CACHE_VALUE_POSITION_POJO = "positionPojo";

    public static final  String CACHE_KEY_POSITION_POJO = "#positionCode";

    public static final  String CACHE_VALUE_SERVICE_POJO = "servicePojo";

    public static final String CACHE_KEY_SERVICE_POJO = "#serviceGroupCode";

    public static final String CACHE_VALUE_ACTIVE_OFFICE_TEMPLATE_POJO = "activeOfficeTemplatePojo";

    public static final String CACHE_KEY_ACTIVE_OFFICE_TEMPLATE_POJO = "#result.getId()";

    public static final String CACHE_VALUE_DETAIL_EATT = "officeDetailEatt";

    public static final String CACHE_VALUE_SECTION_EMPLOYEE = "sectionEmployee";

    public static final String CACHE_CONDITION_SECTION = "#sectionId != null";

    public static final String CACHE_CONDITION_DESIGNATION = "#designationCode != null";

    public static final String CACHE_CONDITION_CODE = "#code != null";

    public static final String CACHE_CONDITION_POSITION_CODE = "#positionCode != null";




}
