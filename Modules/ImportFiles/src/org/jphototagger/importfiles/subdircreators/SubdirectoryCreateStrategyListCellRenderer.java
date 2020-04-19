package org.jphototagger.importfiles.subdircreators;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class SubdirectoryCreateStrategyListCellRenderer implements ListCellRenderer<Object> {

    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();
    private final Icon userDefinedIcon = Icons.getIcon("icon_custom.png");

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof SubdirectoryCreateStrategy) {
            SubdirectoryCreateStrategy strategy = (SubdirectoryCreateStrategy) value;

            label.setText(strategy.getDisplayName());
            setIcon(strategy, label);
            setToolTipText(strategy, label);
        }

        return label;
    }

    private void setIcon(SubdirectoryCreateStrategy strategy, JLabel label) {
        Icon icon = strategy.isUserDefined()
                ? userDefinedIcon
                : null;
        label.setIcon(icon);
    }

    private void setToolTipText(SubdirectoryCreateStrategy strategy, JLabel label) {
        String text = strategy.isUserDefined()
                ? Bundle.getString(SubdirectoryCreateStrategyListCellRenderer.class, "SubdirectoryCreateStrategyListCellRenderer.ToolTipText.UserDefined")
                : "";
        label.setToolTipText(text);
    }
}
