package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererKeywordsEditPanel extends DefaultListCellRenderer {
    private static final Icon ICON = AppLookAndFeel.getIcon("icon_keyword_list.png");
    private static final long serialVersionUID = -3496459487033704492L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        label.setIcon(ICON);

        return label;
    }
}
