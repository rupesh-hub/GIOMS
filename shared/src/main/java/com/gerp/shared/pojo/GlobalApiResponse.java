package com.gerp.shared.pojo;

import com.gerp.shared.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Component
//@Scope("prototype")
public class GlobalApiResponse implements Serializable {
    private ResponseStatus status;
    private String message;
    private Object data;


    public void setResponse(String message, ResponseStatus status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

}
