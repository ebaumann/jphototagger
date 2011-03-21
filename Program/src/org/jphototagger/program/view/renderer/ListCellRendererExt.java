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
        setForeground(AppLookAndFeel.getListForeground());
        setBackground(AppLookAndFeel.getListBackground());
    }

    protected void setColors(int index, boolean selected, JLabel label) {
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = index == tempSelRow;
        boolean isSelection = isTempSelRow || (selected && !tempSelExists);

        label.setForeground((isSelection)
                            ? AppLookAndFeel.getListSelectionForeground()
                            : getForeground());
        label.setBackground((isTempSelRow || (selected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionBackground()
                            : getBackground());
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
