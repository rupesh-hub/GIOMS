package com.gerp.shared.generic.api.pagination.response;

import java.util.List;
import java.util.Map;

public class GetRowsResponse {
    private List<Map<String, Object>> data;
    private int lastRow;
    private List<String> secondaryColumnFields;

    public GetRowsResponse() {
    }

    public GetRowsResponse(List<Map<String, Object>> data, int lastRow, List<String> secondaryColumnFields) {
        this.data = data;
        this.lastRow = lastRow;
        this.secondaryColumnFields = secondaryColumnFields;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    public List<String> getSecondaryColumnFields() {
        return secondaryColumnFields;
    }

    public void setSecondaryColumns(List<String> secondaryColumnFields) {
        this.secondaryColumnFields = secondaryColumnFields;
    }
}
