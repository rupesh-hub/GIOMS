package com.gerp.usermgmt.pojo.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMSScreenModelPojo implements Comparator<TMSScreenModelPojo> {

    private Long id;

    private String source = "productprocess";

    private String code;

    private String name;


    @Override
    public int compare(TMSScreenModelPojo o1, TMSScreenModelPojo o2) {
        return o1.id.compareTo(o2.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TMSScreenModelPojo temp = (TMSScreenModelPojo) o;
        return id.equals(temp.getId()) && source.equals(temp.getSource()) && code.equals(temp.getCode()) && name.equals(temp.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,source, code, name);
    }


}
