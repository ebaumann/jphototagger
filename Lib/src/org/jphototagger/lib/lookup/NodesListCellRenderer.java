package org.jphototagger.lib.lookup;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.api.component.HtmlDisplayNameProvider;
import org.jphototagger.api.nodes.Node;

/**
 * @author Elmar Baumann
 */
public final class NodesListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Node) {
            Node node = (Node) value;
            String displayName = getDisplayName(node);
            Icon smallIcon = node.getSmallIcon();

            label.setText(displayName);
            label.setIcon(smallIcon);
        }

        return label;
    }

    private String getDisplayName(Node node) {
        if (node instanceof HtmlDisplayNameProvider) {
            HtmlDisplayNameProvider provider = (HtmlDisplayNameProvider) node;

            return provider.getHtmlDisplayName();
        }

        return node.getDisplayName();
    }
}
