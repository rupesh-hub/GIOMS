package com.gerp.shared.constants.cacheconstants;


    public final class EmpCacheConst {
        public EmpCacheConst() {throw new IllegalStateException("Utility Class");}
         public static final String CACHE_VALUE_MINIMAL = "employeeMinimal";
         public static final String CACHE_VALUE_DETAIL = "employeeDetail";
        public static final String CACHE_SET_KEY = "#pisCode";
        public static final String CACHE_EVICT_KEY = "#result.getPisCode()";
        public   static final String CACHE_UNLESS_CONDITION = "#result == null";
        public static final  String CACHE_CONDITION = "#pisCode != null";

        public static final String CACHE_VALUE_EMPLOYEE_SECTION = "sectionEmployee";

        public static final String CACHE_SET_PARAMETER_KEY = "#pisCode+'_'+#sectionId";

        public static final String CACHE_VALUE_DELEGATION_RESPONSE_POJO = "delegationResponsePOjo";

        public static final String CACHE_KEY_DELEGATION_RESPONSE_POJO = "#id";

        public static final String CACHE_VALUE_EMPLOYEE_MINIMAL_EATT = "employeeMinimalEatt";

        public static final String CACHE_VALUE_EMPLOYEE_DETAIL_EATT = "employeeDetailEatt";

    }

