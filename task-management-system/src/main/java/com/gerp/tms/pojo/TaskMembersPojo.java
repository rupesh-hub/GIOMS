package com.gerp.tms.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class TaskMembersPojo {

    @JsonIgnore
    private Long id;
    private String memberId;
    private String memberNameEn;
    private String memberNameNp;
    private boolean isAdmin;
}
