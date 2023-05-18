package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusPojo {

    private Long id;
    private Status status;
    private String description;

}
