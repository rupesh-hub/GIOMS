package com.gerp.shared.uuid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdMapper {
    private String guid;
    private Long id;
}
