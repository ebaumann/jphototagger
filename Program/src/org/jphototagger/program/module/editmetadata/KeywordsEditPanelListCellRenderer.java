package org.jphototagger.program.module.editmetadata;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.program.app.ui.AppLookAndFeel;


/**
 * @author Elmar Baumann
 */
public final class KeywordsEditPanelListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON = AppLookAndFeel.getIcon("icon_keyword_list.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        label.setIcon(ICON);

        return label;
    }
}
