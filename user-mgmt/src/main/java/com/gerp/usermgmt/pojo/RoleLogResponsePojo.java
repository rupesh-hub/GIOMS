package com.gerp.usermgmt.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.model.employee.Employee;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleLogResponsePojo {



    Long id;

    IdNamePojo updatedBy;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kathmandu")
    Timestamp updatedDate;

    String roleLogJson;

    List<RoleLogDetailPojo> updatedRoles;


}
