package com.gerp.usermgmt.pojo;

import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {

    private Long id;

    @NotBlank
    @NotNull
    private String key;

    private String name;

    @NotNull
    private Long screenId;

    private String screenName;

    private IdNamePojo screenGroup;

    private List<Long> privilegeList;

    public ModuleDto(Long id, @NotBlank @NotNull String name, @NotNull Long screenId, String screenName, List<Long> privilegeList) {
        this.id = id;
        this.name = name;
        this.screenId = screenId;
        this.screenName = screenName;
        this.privilegeList = privilegeList;
    }
}
