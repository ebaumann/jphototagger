/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.util.logging.LogfileRecord;
import de.elmar_baumann.lib.resource.JslBundle;
import de.elmar_baumann.lib.util.CollectionUtil;
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
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class TableModelLogfiles extends DefaultTableModel {

    private static final long                serialVersionUID = -7886614829435568257L;
    private final        List<LogfileRecord> records         = new ArrayList<LogfileRecord>();
    private final        List<Level>         visibleLevels;
    private final        String              filter;

    public TableModelLogfiles(String filter, List<Level> visibleLevels) {
        if (filter == null) throw new NullPointerException("filter == null");
        if (visibleLevels == null) throw new NullPointerException("visibleLevels == null");

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
            throw new NullPointerException("record == null");

        if ((visibleLevels.contains(Level.ALL) || visibleLevels.contains(record.
                getLevel())) && (filter.isEmpty() || record.contains(filter))) {
            List<Object> row = new ArrayList<Object>();
            row.add(record.getLevel());
            row.add(new Date(record.getMillis()));
            String message = record.getMessage();
            row.add(message == null
                    ? JslBundle.INSTANCE.getString("TableModelLogfiles.Error.MessageIsNull")
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
        if (!CollectionUtil.isValidIndex(records, index)) throw new IllegalArgumentException("Invalid index: " + index + " element count: " + records.size());

        return records.get(index);
    }

    private void addColumns() {
        addColumn(JslBundle.INSTANCE.getString("TableModelLogfiles.HeaderColumn.1"));
        addColumn(JslBundle.INSTANCE.getString("TableModelLogfiles.HeaderColumn.2"));
        addColumn(JslBundle.INSTANCE.getString("TableModelLogfiles.HeaderColumn.3"));
    }

    /**
     * Setzt Datensätze einer Logdatei.
     *
     * @param records Datensätze
     */
    public void setRecords(List<LogfileRecord> records) {
        if (records == null)
            throw new NullPointerException("records == null");

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
