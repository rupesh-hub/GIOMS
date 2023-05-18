package com.gerp.usermgmt.pojo.transfer.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentPojo {

    private Long id;
    private String name;
}
