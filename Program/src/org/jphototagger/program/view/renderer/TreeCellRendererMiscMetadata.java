package org.jphototagger.program.view.renderer;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.exif.ColumnExifFocalLength;
import org.jphototagger.domain.database.xmp.ColumnXmpRating;
import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.metadata.selections.ColumnIcons;
import org.jphototagger.program.model.TreeModelMiscMetadata;

/**
 *
 * @author Elmar Baumann
 */
public final class TreeCellRendererMiscMetadata extends TreeCellRendererExt {
    private static final ImageIcon ICON_MISC_METADATA = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_misc_metadata.png");
    private static final ImageIcon ICON_EXIF = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_exif.png");
    private static final ImageIcon ICON_XMP = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_xmp.png");
    private static final ImageIcon ICON_DETAIL = IconUtil.getImageIcon("/org/jphototagger/program/resource/icons/icon_misc_metadata_detail.png");
    private static final Map<Column, ImageIcon> ICON_OF_COLUMN = new HashMap<Column, ImageIcon>();
    private static final long serialVersionUID = 4497836207990199053L;

    static {
        for (Column exifColumn : TreeModelMiscMetadata.getExifColumns()) {
            ICON_OF_COLUMN.put(exifColumn, ICON_EXIF);
        }

        for (Column xmpColumn : TreeModelMiscMetadata.getXmpColumns()) {
            ICON_OF_COLUMN.put(xmpColumn, ICON_XMP);
        }
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        Object userObject = node.getUserObject();
        Object parentUserObject = (parentNode == null) ? null : parentNode.getUserObject();
        int tempSelRow = getTempSelectionRow();
        boolean tempSelRowIsSelected = tempSelRow < 0 ? false : tree.isRowSelected(tempSelRow);

        setIcon(userObject, parentUserObject, parentNode, (TreeNode) tree.getModel().getRoot(), leaf);
        setText(userObject, parentUserObject);
        setColors(row, selected, tempSelRowIsSelected);

        return this;
    }

    private void setIcon(Object userObject, Object parentUserObject, DefaultMutableTreeNode parentNode, TreeNode root,
                         boolean leaf) {
        if (userObject instanceof Column) {
            setColumnIcon((Column) userObject);
        } else if (leaf) {
            Icon iconDetail = getIconDetail(parentUserObject);

            setIcon((iconDetail == null)
                    ? ICON_DETAIL
                    : iconDetail);
        } else if ((parentNode != null) && parentNode.equals(root)) {
            setIcon(ICON_MISC_METADATA);
        }
    }

    private Icon getIconDetail(Object userObject) {
        if (userObject instanceof Column) {
            return ColumnIcons.getIcon((Column) userObject);
        }

        return null;
    }

    private void setColumnIcon(Column column) {
        if (column != null) {
            ImageIcon icon = ICON_OF_COLUMN.get(column);

            assert icon != null : "No icon defined for column: " + column;

            if (icon != null) {
                setIcon(icon);
            }
        }
    }

    private void setText(Object userObject, Object parentUserObject) {
        if (userObject == null) {
            return;
        }

        DecimalFormat shortFormat = new DecimalFormat("#");
        DecimalFormat doubleFormat = new DecimalFormat("#.#");

        if (userObject instanceof Column) {
            Column col = (Column) (userObject);

            setText(col.getDescription());
        } else if (userObject instanceof String) {
            setText((String) userObject);
        } else if ((userObject instanceof Short) || (userObject instanceof Long)) {
            setText(shortFormat.format(userObject));
        } else if (userObject instanceof Double) {
            setText(doubleFormat.format(userObject) + getTextPostfix(parentUserObject));
        } else if (userObject instanceof Long) {
            setText(Long.toString((Long) userObject) + getTextPostfix(parentUserObject));
        } else {
            assert false : "Unrecognized data type: " + userObject.getClass().getName();
        }
    }

    private String getTextPostfix(Object userObject) {
        if (userObject instanceof Column) {
            Column column = (Column) userObject;

            if (column.equals(ColumnExifFocalLength.INSTANCE)) {
                return " mm";
            } else if (column.equals(ColumnXmpRating.INSTANCE)) {
                return Bundle.getString(TreeCellRendererMiscMetadata.class, "TreeCellRendererMiscMetadata.PostfixColumnXmpRating");
            }
        }

        return "";
    }

    // TreeItemTempSelectionRowSetter calls this reflective not if only in super class defined
    @Override
    public void setTempSelectionRow(int index) {
        super.setTempSelectionRow(index);
    }
}
