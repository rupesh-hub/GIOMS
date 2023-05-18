package com.gerp.shared.generic.api.pagination.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SortModel implements Serializable {

    //    private String colId;
    private String field;
    private String sort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortModel sortModel = (SortModel) o;
        return Objects.equals(field, sortModel.field) &&
                Objects.equals(sort, sortModel.sort);
    }

    @Override
    public int hashCode() {

        return Objects.hash(field, sort);
    }

    @Override
    public String toString() {
        return "SortModel{" +
                "colId='" + field + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
