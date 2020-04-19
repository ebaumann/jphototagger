package org.jphototagger.importfiles.subdircreators.templates;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class SubdirectoryTemplateListCellRenderer implements ListCellRenderer<SubdirectoryTemplate> {

    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();
    private final Icon icon = Icons.getIcon("icon_folder.png");

    @Override
    public Component getListCellRendererComponent(JList<? extends SubdirectoryTemplate> list, SubdirectoryTemplate template, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) delegate.getListCellRendererComponent(list, template, index, isSelected, cellHasFocus);

        label.setText(template.getDisplayName());
        label.setIcon(icon);

        return label;
    }
}
