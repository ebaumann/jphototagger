package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.util.logging.LogfileRecord;
import de.elmar_baumann.lib.resource.Bundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.swing.table.DefaultTableModel;

/**
 * Datensätze mit ausgewählten Spalten einer Logdatei.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelLogfiles extends DefaultTableModel {

    private final List<LogfileRecord> records = new ArrayList<LogfileRecord>();
    private List<Level> visibleLevels = new ArrayList<Level>();
    private String filter = ""; // NOI18N

    public TableModelLogfiles() {
        setVisibleLevels();
        addColumns();
    }

    /**
     * Übernimmt nur Datensätze mit bestimmtem Teilstring. Aufruf vor addRecord()
     * oder setRecords.
     * 
     * @param filterString Filter
     * @see                LogfileRecord#contains(java.lang.String)
     */
    public void setFilter(String filterString) {
        filter = filterString == null ? "" : filterString; // NOI18N
    }

    /**
     * Schränkt die Anzeige auf bestimmte Level ein.
     * 
     * @param levels Anzuzeigende Level
     */
    public void setVisibleLevels(List<Level> levels) {
        visibleLevels = levels;
    }

    /**
     * Fügt einen Logfiledatensatz hinzu.
     * 
     * @param record Datensatz
     */
    public void addRecord(LogfileRecord record) {
        if ((visibleLevels.contains(Level.ALL) || visibleLevels.contains(record.getLevel())) && (filter.isEmpty() || record.contains(filter))) {
            List<Object> row = new ArrayList<Object>();
            row.add(record.getLevel());
            row.add(new Date(record.getMillis()));
            String message = record.getMessage();
            row.add(message == null
                ? Bundle.getString("TableModelLogfiles.ErrorMessage.MessageIsNull")
                : message);
            records.add(record);
            addRow(row.toArray(new Object[row.size()]));
        }
    }

    /**
     * Liefert einen Logfiledatensatz.
     * 
     * @param index Index des Datensatzes
     * @return      Datensatz
     */
    public LogfileRecord getLogfileRecord(int index) {
        return records.get(index);
    }

    private void addColumns() {
        addColumn(Bundle.getString("TableModelLogfiles.HeaderColumn.1"));
        addColumn(Bundle.getString("TableModelLogfiles.HeaderColumn.2"));
        addColumn(Bundle.getString("TableModelLogfiles.HeaderColumn.3"));
    }

    /**
     * Setzt Datensätze einer Logdatei.
     * 
     * @param records Datensätze
     */
    public void setRecords(List<LogfileRecord> records) {
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

    private void setVisibleLevels() {
        visibleLevels.add(Level.ALL);
    }
}
