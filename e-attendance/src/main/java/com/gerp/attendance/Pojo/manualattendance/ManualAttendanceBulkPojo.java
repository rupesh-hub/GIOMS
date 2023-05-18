package com.gerp.attendance.Pojo.manualattendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.KaajDateListPojo;
import com.gerp.attendance.Pojo.EmployeeDetailPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManualAttendanceBulkPojo {
    private List<String> pisCode;

    private IdNamePojo employee;

    @JsonFormat(pattern = "HH:mm:ss")
    private String checkinTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private String checkoutTime;

    private List<MultipartFile> document;

    private Integer orders;

    private Integer id;
    private String pisCodeData;
    private List<EmployeeDetailPojo> manualAttendanceEmployeeDetail;
    private List<KaajDateListPojo> appliedDateList;


    private String appliedDateListData;

    public static KaajDateListPojo parser(Object o) throws InvocationTargetException, IllegalAccessException {
        Map<String, String> data = (Map<String, String>) o;
        KaajDateListPojo kaajDateListPojo = new KaajDateListPojo();
        kaajDateListPojo.setFromDateEn(LocalDate.parse(data.get("fromDateEn")));
        kaajDateListPojo.setToDateEn(LocalDate.parse(data.get("toDateEn")));
        kaajDateListPojo.setFromDateNp(data.get("fromDateNp"));
        kaajDateListPojo.setToDateNp(data.get("toDateNp"));
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
