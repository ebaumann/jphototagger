package org.jphototagger.program.module.metadatatemplates;

import org.jphototagger.program.app.ui.ListCellRendererExt;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplatesListCellRenderer extends ListCellRendererExt {

    private static final ImageIcon ICON = AppLookAndFeel.getIcon("icon_edit.png");
    private static final long serialVersionUID = 8409972246407893544L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String name = ((MetadataTemplate) value).getName();
        boolean tempSelRowIsSelected = getTempSelectionRow() < 0 ? false : list.isSelectedIndex(getTempSelectionRow());

        label.setText(name);
        label.setIcon(ICON);
        setColors(index, isSelected, tempSelRowIsSelected, label);

        return label;
    }

    // ListItemTempSelectionRowSetter calls this reflective not if only in super class defined
    @Override
    public void setTempSelectionRow(int index) {
        super.setTempSelectionRow(index);
    }
}
