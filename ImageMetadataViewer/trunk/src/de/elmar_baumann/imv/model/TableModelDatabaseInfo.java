package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.DatabaseInfoRecordCountColumns;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/**
 * Enthält Informationen über die Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelDatabaseInfo extends DefaultTableModel
        implements DatabaseListener {

    private static final List<DatabaseImageEvent.Type> COUNT_EVENTS =
            new ArrayList<DatabaseImageEvent.Type>();
    private final DatabaseStatistics db = DatabaseStatistics.INSTANCE;
    private final LinkedHashMap<Column, StringBuffer> bufferDifferentOfColumn =
            new LinkedHashMap<Column, StringBuffer>();
    private final LinkedHashMap<Column, StringBuffer> bufferTotalOfColumn =
            new LinkedHashMap<Column, StringBuffer>();
    private boolean listenToDatabase = false;

    static {
        COUNT_EVENTS.add(DatabaseImageEvent.Type.IMAGEFILE_DELETED);
        COUNT_EVENTS.add(DatabaseImageEvent.Type.IMAGEFILE_INSERTED);
        COUNT_EVENTS.add(DatabaseImageEvent.Type.IMAGEFILE_UPDATED);
    }

    private void initBufferOfColumn() {
        List<Column> columns = DatabaseInfoRecordCountColumns.get();
        for (Column column : columns) {
            bufferDifferentOfColumn.put(column, new StringBuffer());
            bufferTotalOfColumn.put(column, new StringBuffer());
        }
    }

    public TableModelDatabaseInfo() {
        initBufferOfColumn();
        addColumnHeaders();
        addRows();
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
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

    private boolean isCountEvent(DatabaseImageEvent.Type type) {
        return COUNT_EVENTS.contains(type);
    }

    private void addColumnHeaders() {
        addColumn(Bundle.getString("TableModelDatabaseInfo.HeaderColumn.1")); // NOI18N
        addColumn(Bundle.getString("TableModelDatabaseInfo.HeaderColumn.2")); // NOI18N
        addColumn(Bundle.getString("TableModelDatabaseInfo.HeaderColumn.3")); // NOI18N
    }

    private void addRows() {
        Set<Column> columns = bufferDifferentOfColumn.keySet();
        for (Column column : columns) {
            addRow(getRow(
                    column,
                    bufferDifferentOfColumn.get(column),
                    bufferTotalOfColumn.get(column)));
        }
    }

    private Object[] getRow(Column rowHeader,
            StringBuffer bufferDifferent, StringBuffer bufferTotal) {

        return new Object[]{rowHeader, bufferDifferent, bufferTotal};
    }

    private void setCount() {
        new SetCountThread().start();
    }

    private class SetCountThread extends Thread {

        public SetCountThread() {
            super();
            setName("Setting count in database info" + " @ " + // NOI18N
                    getClass().getName());
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {
            Set<Column> columns = bufferDifferentOfColumn.keySet();
            for (Column column : columns) {
                setCountToBuffer(bufferDifferentOfColumn.get(column), db.
                        getDistinctCount(column));
                setCountToBuffer(bufferTotalOfColumn.get(column), db.
                        getTotalRecordCount(column));
            }
            fireTableDataChanged();
        }
    }

    private void setCountToBuffer(StringBuffer buffer, Integer count) {
        buffer.replace(0, buffer.length(), count.toString());
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
