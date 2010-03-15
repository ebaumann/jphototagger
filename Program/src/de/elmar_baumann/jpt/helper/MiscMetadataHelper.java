package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.JptSelectionLookup;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.resource.GUI;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public final class MiscMetadataHelper {
    private MiscMetadataHelper() {}

    /**
     * Adds a selected column to the lookup if - and only if - the parent of
     * the selected tree node is a column.
     *
     * @param   path path of the selected tree node
     * @return       added column or null if no column was added
     */
    public static Column addColumnToLookup(TreePath path) {
        JptSelectionLookup.INSTANCE.removeAll();

        Object lpc = path.getLastPathComponent();

        if (lpc instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) lpc;

            if (selNode.isLeaf()) {
                TreeNode parent = selNode.getParent();

                if (parent instanceof DefaultMutableTreeNode) {
                    Object o =
                        ((DefaultMutableTreeNode) parent).getUserObject();

                    if (o instanceof Column) {
                        JptSelectionLookup.INSTANCE.add(Column.class,
                                                        (Column) o);

                        return (Column) o;
                    }
                }
            }
        }

        return null;
    }

    public static void renameSelectedLeafHavingColumnParent() {
        Column column = getParentColumnOfSelectedLeaf();
        String oldValue = getSelectedLeafValue();

        if (column != null && oldValue != null) {
            RenameDeleteXmpValue.rename(column, oldValue);
        }
    }

    public static void deleteSelectedLeafHavingColumnParent() {
        Column column = getParentColumnOfSelectedLeaf();
        String oldValue = getSelectedLeafValue();

        if (column != null && oldValue != null) {
            RenameDeleteXmpValue.delete(column, oldValue);
        }
    }

    public static void deleteChildValueOf(Column column, String value) {
        Object rn = GUI.INSTANCE.getAppPanel().getTreeMiscMetadata().getModel().getRoot();
        if (rn instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) rn;
            for (Enumeration<?> e = root.depthFirstEnumeration(); e.hasMoreElements(); ) {
                Object o = e.nextElement();
                if (o instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                    Object cuo = node.getUserObject();
                    if (cuo instanceof Column && ((Column) cuo).equals(column)) {
                        int count = node.getChildCount();
                        int index = -1;
                        for (int i = 0; i < count; i++) {
                            Object child = node.getChildAt(i);
                            if (child instanceof DefaultMutableTreeNode) {
                                Object uo = ((DefaultMutableTreeNode) child).getUserObject();
                                if (uo instanceof String && ((String) uo).equals(value)) {
                                    index = i;
                                }
                            }
                        }
                        if (index != -1) {
                            node.remove(index);
                            // fire
                        }
                    }
                }
            }
        }
    }

    public static Column getParentColumnOfSelectedLeaf() {
        TreePath tp =
            GUI.INSTANCE.getAppPanel().getTreeMiscMetadata().getSelectionPath();

        if (tp != null) {
            Object lpc = tp.getLastPathComponent();

            if (lpc instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) lpc;

                if (node.isLeaf()) {
                    TreeNode parent = node.getParent();

                    if (parent instanceof DefaultMutableTreeNode) {
                        Object uo =
                            ((DefaultMutableTreeNode) parent).getUserObject();

                        if (uo instanceof Column) {
                            return (Column) uo;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns a selected leaf value if a leaf is selected and the user object
     * of that leaf is a string.
     *
     * @return string or null if no string leaf is selected
     */
    public static String getSelectedLeafValue() {
        TreePath tp =
            GUI.INSTANCE.getAppPanel().getTreeMiscMetadata().getSelectionPath();

        if (tp != null) {
            Object lpc = tp.getLastPathComponent();

            if (lpc instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) lpc;

                if (node.isLeaf()) {
                    Object uo = node.getUserObject();

                    if (uo instanceof String) {
                        return (String) uo;
                    }
                }
            }
        }

        return null;
    }
}
