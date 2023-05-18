package com.gerp.dartachalani.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDetail {

    private String district;

    private String address;

    private String tole;
}
