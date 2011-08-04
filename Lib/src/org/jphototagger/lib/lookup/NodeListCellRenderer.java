package org.jphototagger.lib.lookup;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.api.nodes.Node;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class NodeListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Node) {
            Node node = (Node) value;

            label.setText(node.getHtmlDisplayName());
            label.setIcon(node.getSmallIcon());
        }

        return label;
    }

}
