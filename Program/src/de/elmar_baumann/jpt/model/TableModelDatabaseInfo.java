/*
 * @(#)TableModelDatabaseInfo.java    2008-10-05
 *
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

package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.DatabaseStatistics;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections
    .DatabaseInfoRecordCountColumns;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

/**
 * Elements are {@link Column}s retrieved through
 * {@link DatabaseInfoRecordCountColumns#get()}.
 *
 * This model contains information about the database content, currently the
 * count of table rows. If the database content changes, this model updates
 * itself if set through {@link #setListenToDatabase(boolean)}.
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class TableModelDatabaseInfo extends DefaultTableModel
        implements DatabaseImageFilesListener {
    private static final List<DatabaseImageFilesEvent.Type> COUNT_EVENTS =
        new ArrayList<DatabaseImageFilesEvent.Type>();
    private static final long                         serialVersionUID        =
        1974343527501774916L;
    private final transient DatabaseStatistics        db                      =
        DatabaseStatistics.INSTANCE;
    private final LinkedHashMap<Column, StringBuffer> bufferOfColumn =
        new LinkedHashMap<Column, StringBuffer>();
    private boolean listenToDatabase;

    static {
        COUNT_EVENTS.add(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED);
        COUNT_EVENTS.add(DatabaseImageFilesEvent.Type.IMAGEFILE_INSERTED);
        COUNT_EVENTS.add(DatabaseImageFilesEvent.Type.IMAGEFILE_UPDATED);
    }

    private void initBufferOfColumn() {
        List<Column> columns = DatabaseInfoRecordCountColumns.get();

        for (Column column : columns) {
            bufferOfColumn.put(column, new StringBuffer());
        }
    }

    public TableModelDatabaseInfo() {
        initBufferOfColumn();
        addColumnHeaders();
        addRows();
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        if (listenToDatabase && isCountEvent(event.getType())) {
            update();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        setCount();
    }

    public void setListenToDatabase(boolean listen) {
        listenToDatabase = listen;
    }

    private boolean isCountEvent(DatabaseImageFilesEvent.Type type) {
        return COUNT_EVENTS.contains(type);
    }

    private void addColumnHeaders() {
        addColumn(
            JptBundle.INSTANCE.getString(
                "TableModelDatabaseInfo.HeaderColumn.1"));
        addColumn(
            JptBundle.INSTANCE.getString(
                "TableModelDatabaseInfo.HeaderColumn.2"));
    }

    private void addRows() {
        Set<Column> columns = bufferOfColumn.keySet();

        for (Column column : columns) {
            addRow(getRow(column, bufferOfColumn.get(column)));
        }
    }

    private Object[] getRow(Column rowHeader, StringBuffer bufferDifferent) {
        return new Object[] { rowHeader, bufferDifferent };
    }

    private void setCount() {
        new SetCountThread().start();
    }

    private class SetCountThread extends Thread {
        public SetCountThread() {
            super();
            setName("Setting count in database info @ "
                    + getClass().getSimpleName());
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {
            for (Column column : bufferOfColumn.keySet()) {
                setCountToBuffer(bufferOfColumn.get(column),
                                 db.getTotalRecordCountOf(column));
            }

            fireTableDataChanged();
        }
    }


    private void setCountToBuffer(StringBuffer buffer, Integer count) {
        buffer.replace(0, buffer.length(), count.toString());
    }
}
