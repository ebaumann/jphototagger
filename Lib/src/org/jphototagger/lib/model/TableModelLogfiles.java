package org.jphototagger.lib.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.logging.LogfileRecord;

/**
 *
 * @author Elmar Baumann
 */
public final class TableModelLogfiles extends TableModelExt {

    private static final long serialVersionUID = -7886614829435568257L;
    private final List<LogfileRecord> records = new ArrayList<LogfileRecord>();
    private final List<Level> visibleLevels;
    private final String filter;

    public TableModelLogfiles(String filter, List<Level> visibleLevels) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        if (visibleLevels == null) {
            throw new NullPointerException("visibleLevels == null");
        }

        this.filter = filter;
        this.visibleLevels = visibleLevels;
        addColumns();
    }

    private void addRecord(LogfileRecord record) {
        if (record == null) {
            throw new NullPointerException("record == null");
        }

        if ((visibleLevels.contains(Level.ALL) || visibleLevels.contains(record.getLevel()))
                && (filter.isEmpty() || record.contains(filter))) {
            List<Object> row = new ArrayList<Object>();

            row.add(record.getLevel());
            row.add(new Date(record.getMillis()));

            String message = record.getMessage();

            row.add((message == null)
                    ? Bundle.getString(TableModelLogfiles.class, "TableModelLogfiles.Error.MessageIsNull")
                    : message);
            records.add(record);
            addRow(row.toArray(new Object[row.size()]));
        }
    }

    public LogfileRecord getLogfileRecord(int index) {
        if (!CollectionUtil.isValidIndex(records, index)) {
            throw new IllegalArgumentException("Invalid index: " + index + " element count: " + records.size());
        }

        return records.get(index);
    }

    private void addColumns() {
        addColumn(Bundle.getString(TableModelLogfiles.class, "TableModelLogfiles.HeaderColumn.1"));
        addColumn(Bundle.getString(TableModelLogfiles.class, "TableModelLogfiles.HeaderColumn.2"));
        addColumn(Bundle.getString(TableModelLogfiles.class, "TableModelLogfiles.HeaderColumn.3"));
    }

    public void setRecords(List<LogfileRecord> records) {
        if (records == null) {
            throw new NullPointerException("records == null");
        }

        clear();

        for (LogfileRecord record : records) {
            addRecord(record);
        }

        fireTableDataChanged();
    }

    private void clear() {
        records.clear();

        while (getRowCount() > 0) {
            removeRow(0);
        }
    }
}
