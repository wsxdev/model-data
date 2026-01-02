package com.app.models.services;

import com.app.models.services.records.ColumnHeader;
import com.app.models.services.records.YearDataSummary;

import java.util.List;

public class DataResult {
    private List<YearDataSummary> rows;
    private List<ColumnHeader> columnHeaders;

    public DataResult(List<YearDataSummary> rows, List<ColumnHeader> columnHeaders) {
        this.rows = rows;
        this.columnHeaders = columnHeaders;
    }

    public List<YearDataSummary> getRows() { return rows; }
    public List<ColumnHeader> getColumnHeaders() { return columnHeaders; }
}
