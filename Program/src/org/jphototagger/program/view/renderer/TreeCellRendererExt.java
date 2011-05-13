package org.jphototagger.program.view.renderer;

import java.awt.Color;
import org.jphototagger.program.app.AppLookAndFeel;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 *
 * @author Elmar Baumann
 */
public class TreeCellRendererExt extends DefaultTreeCellRenderer {
    private static final Color TREE_FOREGROUND = AppLookAndFeel.getTreeForeground();
    private static final Color TREE_BACKGROUND = AppLookAndFeel.getTreeBackground();
    private static final Color TREE_SELECTION_FOREGROUND = AppLookAndFeel.getTreeSelectionForeground();
    private static final Color TREE_SELECTION_BACKGROUND = AppLookAndFeel.getTreeSelectionBackground();
    private static final long serialVersionUID = 7468243064122106211L;
    private int tempSelectionRow = -1;

    public TreeCellRendererExt() {
        setOpaque(true);
        setForeground(TREE_FOREGROUND);
        setBackground(TREE_BACKGROUND);
    }

    protected void setColors(int row, boolean itemAtIndexIsSelected, boolean tempSelRowIsSelected) {
        boolean isTempSelRow = row == tempSelectionRow;
        boolean tempSelExists = tempSelectionRow >= 0;
        boolean isSelection = isTempSelRow
                                  || (!tempSelExists && itemAtIndexIsSelected)
                                  || (tempSelExists && !isTempSelRow && itemAtIndexIsSelected && tempSelRowIsSelected);

        setForeground(isSelection
                      ? TREE_SELECTION_FOREGROUND
                      : TREE_FOREGROUND);
        setBackground(isSelection
                      ? TREE_SELECTION_BACKGROUND
                      : TREE_BACKGROUND);
    }

    public void setTempSelectionRow(int index) {
        tempSelectionRow = index;
    }

    public int getTempSelectionRow() {
        return tempSelectionRow;
    }
}
