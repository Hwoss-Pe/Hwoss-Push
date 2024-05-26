package com.hwoss.cron.csv;

import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvRowHandler;

public class CountFileRowHandler implements CsvRowHandler {
    private long rowSize;

    @Override
    public void handle(CsvRow csvRow) {
        rowSize++;
    }

    public long getRowSize() {
        return rowSize;
    }
}
