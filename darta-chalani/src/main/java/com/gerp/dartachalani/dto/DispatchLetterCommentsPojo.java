package com.gerp.dartachalani.dto;

import com.gerp.shared.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class DispatchLetterCommentsPojo {
    private Long id;
     private Status cStatus;
    private String description;
    private String dateNp;
    private LocalDate date;
}
