package org.jphototagger.program.module.timeline;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.app.ui.TreeCellRendererExt;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class TimelineTreeCellRenderer extends TreeCellRendererExt {

    private static final ImageIcon ICON_YEAR = Icons.getIcon("icon_timeline.png");
    private static final ImageIcon ICON_MONTH = Icons.getIcon("icon_timeline_month.png");
    private static final ImageIcon ICON_DAY = Icons.getIcon("icon_timeline_day.png");
    private static final ImageIcon ICON_UNKNOWN = Icons.getIcon("icon_timeline_unknown.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        TreeNode root = node.getRoot();

        if (userObject instanceof Timeline.Date) {
            Timeline.Date date = (Timeline.Date) userObject;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

            if (parent != null) {
                boolean isYear = parent.equals(root);
                boolean isMonth = node.getPath().length == 3;

                setIcon(isYear
                        ? ICON_YEAR
                        : isMonth
                        ? ICON_MONTH
                        : ICON_DAY);
                setText(isYear
                        ? String.valueOf(date.year)
                        : isMonth
                        ? date.getMonthDisplayName()
                        : String.valueOf(date.day));
            }
        } else if (node.equals(Timeline.getUnknownNode())) {
            setIcon(ICON_UNKNOWN);
            setText(node.getUserObject().toString());
        }

        int tempSelRow = getTempSelectionRow();
        boolean tempSelRowIsSelected = tempSelRow < 0 ? false : tree.isRowSelected(tempSelRow);

        setColors(row, selected, tempSelRowIsSelected);

        return this;
    }

    // TreeItemTempSelectionRowSetter calls this reflective not if only in super class defined
    @Override
    public void setTempSelectionRow(int index) {
        super.setTempSelectionRow(index);
    }
}
