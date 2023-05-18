package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KaajAppliedOthersPojo {
    private Integer orders;
    private Integer id;
    private List<String> pisCode;
    private String pisCodeData;
    private List<EmployeeDetailPojo> kaajEmployeeDetail;
    private List<KaajDateListPojo> appliedDateList;


    private String appliedDateListData;

    public static KaajDateListPojo parser(Object o) throws InvocationTargetException, IllegalAccessException {
        Map<String, String> data = (Map<String, String>) o;
        KaajDateListPojo kaajDateListPojo = new KaajDateListPojo();
        kaajDateListPojo.setFromDateEn(LocalDate.parse(data.get("fromDateEn")));
        kaajDateListPojo.setToDateEn(LocalDate.parse(data.get("fromDateEn")));
        kaajDateListPojo.setFromDateNp(data.get("fromDateNp"));
        kaajDateListPojo.setToDateNp(data.get("toDateNp"));
        kaajDateListPojo.setDurationType(data.get("durationType"));
        return kaajDateListPojo;
    }

    public void setPisCodeData(String pisCodeData) {
        this.pisCode = new Gson().fromJson(pisCodeData, new TypeToken<List<String>>() {
        }.getType());
    }

    public void setAppliedDateListData(String appliedDateListData) {
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(appliedDateListData.trim()));
            jsonReader.setLenient(true);
            List<Object> datas = new Gson().fromJson(jsonReader, new TypeToken<List<Object>>() {
            }.getType());

            List<KaajDateListPojo> kaajDateListPojos = new ArrayList<>();

            datas.forEach(o -> {
                try {
                    kaajDateListPojos.add(parser(o));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            this.setAppliedDateList(kaajDateListPojos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
