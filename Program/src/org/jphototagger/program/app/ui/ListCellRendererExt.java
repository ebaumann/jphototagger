package org.jphototagger.program.app.ui;

import java.awt.Color;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;


/**
 * @author Elmar Baumann
 */
public class ListCellRendererExt extends DefaultListCellRenderer {

    private static final Color LIST_FOREGROUND = AppLookAndFeel.getListForeground();
    private static final Color LIST_BACKGROUND = AppLookAndFeel.getListBackground();
    private static final Color LIST_SELECTION_FOREGROUND = AppLookAndFeel.getListSelectionForeground();
    private static final Color LIST_SELECTION_BACKGROUND = AppLookAndFeel.getListSelectionBackground();
    private static final long serialVersionUID = 1L;
    private int tempSelectionRow = -1;

    public ListCellRendererExt() {
        setOpaque(true);
        setForeground(LIST_FOREGROUND);
        setBackground(LIST_BACKGROUND);
    }

    protected void setColors(int index, boolean itemAtIndexIsSelected, boolean tempSelRowIsSelected, JLabel label) {
        boolean isTempSelRow = index == tempSelectionRow;
        boolean tempSelExists = tempSelectionRow >= 0;
        boolean isSelection = isTempSelRow
                || (!tempSelExists && itemAtIndexIsSelected)
                || (tempSelExists && !isTempSelRow && itemAtIndexIsSelected && tempSelRowIsSelected);

        label.setForeground(isSelection
                ? LIST_SELECTION_FOREGROUND
                : LIST_FOREGROUND);
        label.setBackground(isSelection
                ? LIST_SELECTION_BACKGROUND
                : LIST_BACKGROUND);
    }

    public int getTempSelectionRow() {
        return tempSelectionRow;
    }

    public void setTempSelectionRow(int index) {
        tempSelectionRow = index;
    }
}
