package com.gerp.usermgmt.pojo.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PisCodeToDeviceMapperPojo {

    @NotNull
    private String pisCode;

//    @NotNull
    // if we have to remove device id then that's why not null was removed
    private Long deviceID;
    
    @JsonIgnore
    private String officeCode;

}
