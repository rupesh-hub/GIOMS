package com.gerp.usermgmt.config;

import java.util.*;

public class MockDataPermissionRepo {
   static Map<String, String> getMapData() {
       Map<String, String> mapDatas = new HashMap<>();
       mapDatas.put("MYOFFICEEMPLOYEE_MYOFFICEEMPLOYEE" , "/api");
       return mapDatas;
   }

    static List<Object>  getMockListData(String endPoint) {
        return Collections.singletonList("MYOFFICEEMPLOYEE_MYOFFICEEMPLOYEE");
    }
}
