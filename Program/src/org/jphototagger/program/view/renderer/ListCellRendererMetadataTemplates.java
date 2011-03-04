package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.MetadataTemplate;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererMetadataTemplates extends ListCellRendererExt {
    private static final ImageIcon ICON = AppLookAndFeel.getIcon("icon_edit.png");
    private static final long serialVersionUID = 8409972246407893544L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String name = ((MetadataTemplate) value).getName();

        label.setText(name);
        label.setIcon(ICON);
        setColors(index, isSelected, label);

        return label;
    }

    @Override
    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
