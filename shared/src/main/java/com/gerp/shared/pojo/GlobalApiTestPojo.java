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
public class GlobalApiTestPojo implements Serializable {

        private ResponseStatus status;
        private String message;
        private List<Object> data;

}
