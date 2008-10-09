package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.selections.AllTables;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Table;
import de.elmar_baumann.imv.database.metadata.collections.ColumnCollectionsSequenceNumber;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesThumbnail;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
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
public class TableModelDatabaseInfo extends DefaultTableModel
    implements DatabaseListener {

    private Database db = Database.getInstance();
    private LinkedHashMap<Column, StringBuffer> bufferOfColumn = new LinkedHashMap<Column, StringBuffer>();
    private List<Column> excludedColumns = new ArrayList<Column>();
    private boolean listenToDatabase = false;

    private void initBufferOfColumn() {
        List<Table> tables = AllTables.get();
        for (Table table : tables) {
            for (Column column : table.getColumns()) {
                if (isInfoColumn(column)) {
                    bufferOfColumn.put(column, new StringBuffer());
                }
            }
        }
    }

    private void initExcludedColumns() {
        excludedColumns.add(ColumnFilesThumbnail.getInstance());
        excludedColumns.add(ColumnCollectionsSequenceNumber.getInstance());
    }

    private boolean isInfoColumn(Column column) {
        return !column.isPrimaryKey() &&
            !column.isForeignKey() &&
            !excludedColumns.contains(column);
    }

    public TableModelDatabaseInfo() {
        initExcludedColumns();
        initBufferOfColumn();
        addColumnHeaders();
        addRows();
        db.addDatabaseListener(this);
    }

    /**
     * Aktualisiert die Information. Ist nur einmalig aufzurufen, nachdem dei
     * Datenbankverbindung steht.
     */
    public void update() {
        setCount();
    }

    public void setListenToDatabase(boolean listen) {
        listenToDatabase = listen;
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (listenToDatabase) {
            update();
        }
    }

    private void addColumnHeaders() {
        addColumn(Bundle.getString("TableModelDatabaseInfo.HeaderColumn.1"));
        addColumn(Bundle.getString("TableModelDatabaseInfo.HeaderColumn.2"));
    }

    private void addRows() {
        Set<Column> columns = bufferOfColumn.keySet();
        for (Column column : columns) {
            addRow(getRow(column, bufferOfColumn.get(column)));
        }
    }

    private Object[] getRow(Column rowHeader, StringBuffer count) {
        return new Object[]{rowHeader, count};
    }

    private void setCountToBuffer(StringBuffer buffer, Integer count) {
        buffer.replace(0, buffer.length(), count.toString());
        fireTableDataChanged();
    }

    private void setCount() {
        Set<Column> columns = bufferOfColumn.keySet();
        for (Column column : columns) {
            setCountToBuffer(bufferOfColumn.get(column),
                db.getDistinctCount(column));
        }
    }
}
