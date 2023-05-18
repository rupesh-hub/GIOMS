package com.gerp.shared.pojo;

import com.gerp.shared.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiDataListPojo implements Serializable {

    private ResponseStatus status;
    private String message;
    private List<String> data;

    public void setResponse(String message, ResponseStatus status, List<String> data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
}
