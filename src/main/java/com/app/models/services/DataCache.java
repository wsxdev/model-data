package com.app.models.services;

import com.app.models.services.records.ColumnHeader;
import com.app.models.services.records.YearDataSummary;

import java.util.List;

public class DataCache {
    private static final DataCache INSTANCE = new DataCache();

    private List<YearDataSummary> lastRows;
    private List<ColumnHeader> lastHeaders;
    private String lastSelection;

    private DataCache() {}
    public static DataCache getInstance() { return INSTANCE; }

    public void put(String selection, List<YearDataSummary> rows, List<ColumnHeader> headers) {
        this.lastSelection = selection;
        this.lastRows = rows;
        this.lastHeaders = headers;
    }

    // MALDITA SEA, NO SABÍA QUÉ PONER :)
    public boolean hasCache() {
        return lastRows != null && lastHeaders != null && lastSelection != null;
    }
    public List<YearDataSummary> getLastRows() { return lastRows; }
    public String getLastSelection() { return lastSelection; }
    public List<ColumnHeader> getLastHeaders() { return lastHeaders; }

    public void clearCache() {
        lastRows = null;
        lastHeaders = null;
        lastSelection = null;
    }
}
