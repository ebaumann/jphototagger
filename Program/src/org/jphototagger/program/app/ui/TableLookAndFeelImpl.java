package org.jphototagger.program.app.ui;

import javax.swing.JComponent;
import org.jphototagger.api.branding.TableLookAndFeel;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = TableLookAndFeel.class)
public final class TableLookAndFeelImpl extends MetadataLabelFormatter implements TableLookAndFeel {

    @Override
    public void setTableCellColor(JComponent tableCell, boolean isSelected) {
        MetadataLabelFormatter.setDefaultCellColors(tableCell, isSelected);
    }

    @Override
    public void setTableCellFont(JComponent tableCell) {
        super.setContentFont(tableCell);
    }

    @Override
    public void setTableRowHeaderFont(JComponent tableHeader) {
        super.setHeaderFont(tableHeader);
    }

    @Override
    public int getRowHeaderMaxChars() {
        return AppLookAndFeel.TABLE_MAX_CHARS_ROW_HEADER;
    }

    @Override
    public int getCellMaxChars() {
        return AppLookAndFeel.TABLE_MAX_CHARS_CELL;
    }

    @Override
    public String getRowHeaderCss() {
        return AppLookAndFeel.TABLE_ROW_HEADER_CSS;
    }

    @Override
    public String getCellCss() {
        return AppLookAndFeel.TABLE_CELL_CSS;
    }
}
