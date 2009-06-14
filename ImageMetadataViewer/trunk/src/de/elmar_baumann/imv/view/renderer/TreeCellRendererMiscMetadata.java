package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.model.TreeModelMiscMetadata;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 * Renders items and text for
 * {@link de.elmar_baumann.imv.model.TreeModelMiscMetadata} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeCellRendererMiscMetadata extends DefaultTreeCellRenderer {

    private static final ImageIcon iconMiscMetadata = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_misc_metadata.png");
    private static final ImageIcon iconExif = IconUtil.getImageIcon(
            "/de/elmar_baumann/imv/resource/icons/icon_exif.png");
    private static final Map<Column, ImageIcon> iconOfColumn =
            new HashMap<Column, ImageIcon>();


    static {
        iconOfColumn.put(ColumnExifFocalLength.INSTANCE, iconExif);
        iconOfColumn.put(ColumnExifIsoSpeedRatings.INSTANCE, iconExif);
        iconOfColumn.put(ColumnExifRecordingEquipment.INSTANCE, iconExif);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.
                getParent();
        Object userObject = node.getUserObject();
        Object parentUserObject = parentNode == null
                                  ? null
                                  : parentNode.getUserObject();
        setIcon(parentUserObject, userObject, parentNode,
                (TreeNode) tree.getModel().getRoot());
        setText(userObject, parentUserObject);

        return this;
    }

    private void setIcon(Object parentUserObject, Object userObject,
            DefaultMutableTreeNode parentNode, TreeNode root) {
        if (userObject instanceof Column) {
            setColumnIcon((Column) userObject);
        } else if (parentUserObject instanceof Column) {
            setColumnIcon((Column) parentUserObject);
        } else if (parentNode != null && parentNode.equals(root)) {
            setIcon(iconMiscMetadata);
        }
    }

    private void setColumnIcon(Column column) {
        if (column != null) {
            ImageIcon icon = iconOfColumn.get(column);
            assert icon != null : "No icon defined for column: " + column;
            if (icon != null) {
                setIcon(icon);
            }
        }
    }

    private void setText(Object userObject, Object parenUObject) {
        if (userObject == null) return;
        DecimalFormat shortFormat = new DecimalFormat("#");
        DecimalFormat doubleFormat = new DecimalFormat("#.#");
        if (userObject instanceof Column) {
            Column col = (Column) (userObject);
            setText(col.getDescription());
        } else if (userObject instanceof String) {
            setText((String) userObject);
        } else if (userObject instanceof Short) {
            setText(shortFormat.format(userObject));
        } else if (userObject instanceof Double) {
            setText(doubleFormat.format(userObject) + getTextPostfix(
                    parenUObject));
        } else {
            assert false : "Unrecognized data type: " + userObject.getClass().
                    getName();
        }
    }

    private String getTextPostfix(Object userObject) {
        if (userObject instanceof Column) {
            Column column = (Column) userObject;
            if (column.equals(ColumnExifFocalLength.INSTANCE)) {
                return " mm";
            }
        }
        return "";
    }
}
