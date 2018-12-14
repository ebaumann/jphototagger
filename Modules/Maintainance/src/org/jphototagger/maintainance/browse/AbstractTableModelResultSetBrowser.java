package org.jphototagger.maintainance.browse;

import java.util.Objects;
import javax.swing.table.DefaultTableModel;
import org.jphototagger.domain.repository.browse.ResultSetBrowser;

/**
 * Populates a table model while browsing a ResultSet. A derived class handles
 * the result via implementing
 * {@link ResultSetBrowser#finished(org.jphototagger.domain.repository.ResultSetBrowser.Result)}.
 *
 * @author Elmar Baumann
 */
public abstract class AbstractTableModelResultSetBrowser implements ResultSetBrowser {

    private final DefaultTableModel tableModel;

    public AbstractTableModelResultSetBrowser(DefaultTableModel tableModel) {
        this.tableModel = Objects.requireNonNull(tableModel, "tableModel == null");
    }

    @Override
    public void columnNames(Object[] columnNames) {
        tableModel.setColumnIdentifiers(columnNames);
    }

    @Override
    public void row(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    protected DefaultTableModel getTableModel() {
        return tableModel;
    }
}
