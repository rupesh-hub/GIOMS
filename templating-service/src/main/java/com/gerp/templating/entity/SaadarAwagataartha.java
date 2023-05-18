package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaadarAwagataartha {

    private String office;

    private String section;

    private String address;

    private String remarks;

    private boolean isExternal = false;
}
