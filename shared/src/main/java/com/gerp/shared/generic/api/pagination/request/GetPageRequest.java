package com.gerp.shared.generic.api.pagination.request;

import com.gerp.shared.generic.api.pagination.filter.CustomFilter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

@AllArgsConstructor
@Data
public class GetPageRequest {
    private int page, limit;

    private String dataType;

    //not used
    // row group columns
    private List<ColumnVO> rowGroupCols;

    // value columns
    private List<ColumnVO> valueCols;

    // pivot columns
    private List<ColumnVO> pivotCols;

    // true if pivot mode is one, otherwise false
    private boolean pivotMode;

    // what groups the user is viewing
    private List<String> groupKeys;
    //not used

    // if filtering, what the filter model is
//    private Map<String, ColumnFilter> filterModel;
    private List<CustomFilter> filterModel;

    // if sorting, what the sort model is
    private List<SortModel> sortModel;

    public GetPageRequest() {
        this.rowGroupCols = emptyList();
        this.valueCols = emptyList();
        this.pivotCols = emptyList();
        this.groupKeys = emptyList();
        this.filterModel = emptyList();
        this.sortModel = emptyList();
    }

    public int getPage() {
        return page - 1;
    }


}
