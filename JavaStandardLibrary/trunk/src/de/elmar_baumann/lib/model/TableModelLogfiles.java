package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.util.logging.LogfileRecord;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.swing.table.DefaultTableModel;

/**
 * Datensätze mit ausgewählten Spalten einer Logdatei.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelLogfiles extends DefaultTableModel {

    private final List<LogfileRecord> records = new ArrayList<LogfileRecord>();
    private final List<Level> visibleLevels;
    private final String filter;

    public TableModelLogfiles(String filter, List<Level> visibleLevels) {
        if (filter == null)
            throw new NullPointerException("filter == null"); // NOI18N
        if (visibleLevels == null)
            throw new NullPointerException("visibleLevels == null"); // NOI18N

        this.filter = filter;
        this.visibleLevels = visibleLevels;
        addColumns();
    }

    /**
     * Fügt einen Logfiledatensatz hinzu.
     * 
     * @param record Datensatz
     */
    public void addRecord(LogfileRecord record) {
        if (record == null)
            throw new NullPointerException("record == null"); // NOI18N

        if ((visibleLevels.contains(Level.ALL) || visibleLevels.contains(record.
                getLevel())) && (filter.isEmpty() || record.contains(filter))) {
            List<Object> row = new ArrayList<Object>();
            row.add(record.getLevel());
            row.add(new Date(record.getMillis()));
            String message = record.getMessage();
            row.add(message == null
                    ? Bundle.getString(
                    "TableModelLogfiles.Error.MessageIsNull") // NOI18N
                    : message);
            records.add(record);
            addRow(row.toArray(new Object[row.size()]));
        }
    }

    /**
     * Liefert einen Logfiledatensatz.
     * 
     * @param  index  Index des Datensatzes
     * @return Datensatz
     * @throws IllegalArgumentException if the index is not valid
     */
    public LogfileRecord getLogfileRecord(int index) {
        if (!ArrayUtil.isValidIndex(records, index))
            throw new IllegalArgumentException("Invalid index: " + index + // NOI18N
                    " element count: " + records.size()); // NOI18N

        return records.get(index);
    }

    private void addColumns() {
        addColumn(Bundle.getString("TableModelLogfiles.HeaderColumn.1")); // NOI18N
        addColumn(Bundle.getString("TableModelLogfiles.HeaderColumn.2")); // NOI18N
        addColumn(Bundle.getString("TableModelLogfiles.HeaderColumn.3")); // NOI18N
    }

    /**
     * Setzt Datensätze einer Logdatei.
     * 
     * @param records Datensätze
     */
    public void setRecords(List<LogfileRecord> records) {
        if (records == null)
            throw new NullPointerException("records == null"); // NOI18N

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
