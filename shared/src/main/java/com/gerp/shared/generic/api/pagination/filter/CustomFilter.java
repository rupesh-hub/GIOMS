package com.gerp.shared.generic.api.pagination.filter;

import com.gerp.shared.generic.api.specification.Filter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomFilter {

    private String field;
    private String filter = Filter.CONTAINS;
    private String searchValue;

    public CustomFilter(String field, String searchValue) {
        this.field = field;
        this.searchValue = searchValue;
    }
}
