package com.gerp.tms.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhaseResponsePojo {
        private Long id ;
        private String phaseName;
        private String phaseNameNp;
        private Boolean active;

}
