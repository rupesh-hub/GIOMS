package com.gerp.dartachalani.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bodartha {

    private String office;

    private String section;

    private String address;

    private Integer order;

    private String remarks;

    private boolean isExternal = false;

}
