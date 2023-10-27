package org.jphototagger.program.module.editmetadata;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.resources.Icons;


/**
 * @author Elmar Baumann
 */
public final class KeywordsEditPanelListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON = Icons.getIcon("icon_keyword_list.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setIcon(ICON);
        if (value instanceof String) {
            label.setText((String) value + "  ");
        }
        return label;
    }
}
