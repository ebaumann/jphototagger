package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 *
 * @author Elmar Baumann
 */
public class TreeCellRendererExt extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 7468243064122106211L;
    protected int tempSelRow = -1;

    public TreeCellRendererExt() {
        setOpaque(true);
    }

    protected void setColors(int row, boolean selected) {
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = row == tempSelRow;

        setForeground((isTempSelRow || (selected &&!tempSelExists))
                      ? AppLookAndFeel.getTreeSelectionForeground()
                      : AppLookAndFeel.getTreeTextForeground());
        setBackground((isTempSelRow || (selected &&!tempSelExists))
                      ? AppLookAndFeel.getTreeSelectionBackground()
                      : AppLookAndFeel.getTreeTextBackground());
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
