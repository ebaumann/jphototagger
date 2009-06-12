package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.data.Timeline;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Component;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 * Renders items and text for {@link de.elmar_baumann.imv.data.Timeline} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeCellRendererTimeline extends DefaultTreeCellRenderer {

    private static final ImageIcon iconYear = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_timeline.png");
    private static final ImageIcon iconMonth = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_timeline_month.png");
    private static final ImageIcon iconDay = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_timeline_day.png");
    private static final ImageIcon iconUnknown = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_timeline_unknown.png");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        TreeNode root = node.getRoot();
        if (userObject instanceof Calendar) {
            Calendar cal = (Calendar) userObject;
            boolean isYear = node.getParent().equals(root);
            boolean isMonth = !node.isLeaf() && !node.getParent().equals(root);
            setIcon(isYear
                    ? iconYear
                    : isMonth
                      ? iconMonth
                      : iconDay);
            setText(isYear
                    ? String.valueOf(cal.get(Calendar.YEAR))
                    : isMonth
                      ? cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                    Locale.getDefault())
                      : String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        } else if (node.equals(Timeline.getUnknownNode())) {
            setIcon(iconUnknown);
            setText(node.getUserObject().toString());
        }
        return this;
    }
}
