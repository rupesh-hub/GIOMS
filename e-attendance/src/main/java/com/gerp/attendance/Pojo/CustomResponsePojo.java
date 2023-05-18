package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponsePojo implements Serializable {
    private Long ResultType;
    private String Message;
    private Long ReturnId;
    private List<Object> dataRow;

    public void setResponse(String Message, Long ResultType, Long ReturnId,List<Object> dataRow) {
        this.Message = Message;
        this.ResultType = ResultType;
        this.ReturnId=ReturnId;
        this.dataRow=dataRow;
    }
}
