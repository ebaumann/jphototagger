package org.jphototagger.importfiles;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.api.component.DisplayNameProvider;

/**
 * @author Elmar Baumann
 */
public final class SubdirectoryCreateStrategyListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setText(((DisplayNameProvider) value).getDisplayName());
        return label;
    }
}
