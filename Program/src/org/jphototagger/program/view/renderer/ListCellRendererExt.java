package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;

/**
 *
 *
 * @author Elmar Baumann
 */
public class ListCellRendererExt extends DefaultListCellRenderer {
    private static final long serialVersionUID = 7531004273695822498L;
    protected int tempSelRow = -1;

    public ListCellRendererExt() {
        setOpaque(true);
    }

    protected void setColors(int index, boolean selected, JLabel label) {
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = index == tempSelRow;

        label.setForeground((isTempSelRow || (selected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionForeground()
                            : AppLookAndFeel.getListForeground());
        label.setBackground((isTempSelRow || (selected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionBackground()
                            : AppLookAndFeel.getListBackground());
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
