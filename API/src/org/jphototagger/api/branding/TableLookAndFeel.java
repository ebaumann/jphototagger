package org.jphototagger.api.branding;

import javax.swing.JComponent;

/**
 * @author Elmar Baumann
 */
public interface TableLookAndFeel {

    void setTableCellColor(JComponent tableCell, boolean isSelected);

    void setTableCellFont(JComponent tableCell);

    void setTableRowHeaderFont(JComponent tableHeader);

    int getRowHeaderMaxChars();

    int getCellMaxChars();

    String getRowHeaderCss();

    String getCellCss();
}
