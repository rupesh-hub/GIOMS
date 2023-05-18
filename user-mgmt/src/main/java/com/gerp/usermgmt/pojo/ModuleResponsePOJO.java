package com.gerp.usermgmt.pojo;

import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleResponsePOJO {
private Long id;
private String name;
private IdNamePojo individualScreen;
private List<IdNamePojo> privilegeList;
}
