package com.app.models.services.records;

import java.util.List;

public record DataResult(List<YearDataSummary> rows, List<ColumnHeader> columnHeaders) { }
