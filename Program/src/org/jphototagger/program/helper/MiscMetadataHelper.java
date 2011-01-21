package org.jphototagger.program.helper;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.data.ColumnData;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.io.ImageUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;

import java.awt.EventQueue;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MiscMetadataHelper {
    private static final List<Column> XMP_COLUMNS = XmpColumns.get();

    public static void saveToImageFiles(List<ColumnData> colData,
            Collection<? extends File> imageFiles) {
        if (colData == null) {
            throw new NullPointerException("colData == null");
        }

        if (imageFiles == null) {
            throw new NullPointerException("imageFile == null");
        }

        List<Pair<File, Xmp>> saveList = new ArrayList<Pair<File, Xmp>>();

        for (File imageFile : imageFiles) {
            if (imageFile.exists() && ImageUtil.checkImageEditable(imageFile)) {
                Xmp xmp = null;

                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(MiscMetadataHelper.class.getName()).log(
                        Level.SEVERE, null, ex);
                }

                if (xmp == null) {
                    xmp = new Xmp();
                }

                for (ColumnData data : colData) {
                    xmp.setValue(data.getColumn(), data.getData());
                }

                saveList.add(new Pair<File, Xmp>(imageFile, xmp));
            }
        }

        if (!saveList.isEmpty()) {
            SaveXmp.save(saveList);
        }
    }

    public static void saveToImageFile(List<ColumnData> colData,
                                       File imageFile) {
        if (colData == null) {
            throw new NullPointerException("colData == null");
        }

        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        saveToImageFiles(colData, Collections.singleton(imageFile));
    }

    public static void addMetadataToSelectedImages(
            final Collection<? extends DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        final List<Column> xmpColumns = XmpColumns.get();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditMetadataPanels editPanels =
                    GUI.getAppPanel().getEditMetadataPanels();

                if (!editPanels.isEditable()) {
                    return;
                }

                for (DefaultMutableTreeNode node : nodes) {
                    if (isParentUserObjectAColumnOf(node, xmpColumns)) {
                        String                 text =
                            (String) node.getUserObject();
                        DefaultMutableTreeNode parent =
                            (DefaultMutableTreeNode) node.getParent();
                        Column column = (Column) parent.getUserObject();

                        editPanels.addText(column, text);
                    }
                }
            }
        });
    }

    public static void removeMetadataFromSelectedImages(
            final Collection<? extends DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        final List<Column> xmpColumns = XmpColumns.get();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditMetadataPanels editPanels =
                    GUI.getAppPanel().getEditMetadataPanels();

                if (!editPanels.isEditable()) {
                    return;
                }

                for (DefaultMutableTreeNode node : nodes) {
                    if (isParentUserObjectAColumnOf(node, xmpColumns)) {
                        String                 text =
                            (String) node.getUserObject();
                        DefaultMutableTreeNode parent =
                            (DefaultMutableTreeNode) node.getParent();
                        Column column = (Column) parent.getUserObject();

                        editPanels.removeText(column, text);
                    }
                }
            }
        });
    }

    public static void addMetadataToSelectedImages(final Column column,
            final String text) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditMetadataPanels editPanels =
                    GUI.getAppPanel().getEditMetadataPanels();

                if (editPanels.isEditable()) {
                    editPanels.addText(column, text);
                }
            }
        });
    }

    public static void removeMetadataFromSelectedImages(final Column column,
            final String text) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditMetadataPanels editPanels =
                    GUI.getAppPanel().getEditMetadataPanels();

                if (editPanels.isEditable()) {
                    editPanels.removeText(column, text);
                }
            }
        });
    }

    /**
     * Returns wether the parent's user object of a specific node is column
     * contained in a collection of columns.
     *
     * @param  node    node
     * @param  columns columns
     * @return         true if the parent's user object is a column contained in
     *                 <code>columns</code>
     */
    public static boolean isParentUserObjectAColumnOf(
            DefaultMutableTreeNode node, Collection<? extends Column> columns) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode) node.getParent();
        Object userObject = parent.getUserObject();

        if (userObject instanceof Column) {
            return columns.contains((Column) userObject);
        }

        return false;
    }

    private static DefaultTreeModel getModel() {
        return (DefaultTreeModel) GUI.getAppPanel().getTreeMiscMetadata()
            .getModel();
    }

    /**
     * Returns the first node with a specific column as user object.
     *
     * @param  column column
     * @return        node with that column as user object
     */
    public static DefaultMutableTreeNode findNodeContains(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        DefaultMutableTreeNode root =
            (DefaultMutableTreeNode) getModel().getRoot();

        for (Enumeration<?> e = root.depthFirstEnumeration();
                e.hasMoreElements(); ) {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) e.nextElement();
            Object userObject = node.getUserObject();

            if ((userObject instanceof Column)
                    && ((Column) userObject).equals(column)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Removes from the model a node with a string value as user object from a
     * parent containing a specific column as user object.
     *
     * @param column column of parent
     * @param value  value of child
     */
    public static void removeChildValueFrom(final Column column,
            final String value) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (value == null) {
            throw new NullPointerException("value == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultMutableTreeNode node = findNodeContains(column);

                if (node != null) {
                    int count = node.getChildCount();

                    for (int i = 0; i < count; i++) {
                        DefaultMutableTreeNode childNode =
                            (DefaultMutableTreeNode) node.getChildAt(i);
                        Object uo = (childNode).getUserObject();

                        if ((uo instanceof String)
                                && ((String) uo).equals(value)) {
                            getModel().removeNodeFromParent(childNode);

                            return;
                        }
                    }
                }
            }
        });
    }

    /**
     *
     * @param  paths
     * @return       values or empty list
     */
    public static List<Pair<Column,
                            String>> getColValuesFrom(List<TreePath> paths) {
        if (paths == null) {
            throw new NullPointerException("paths == null");
        }

        List<Pair<Column, String>> values = new ArrayList<Pair<Column,
                                                String>>(paths.size());

        for (TreePath path : paths) {
            Pair<Column, String> value = getColValueFrom(path);

            if (value != null) {
                values.add(value);
            }
        }

        return values;
    }

    /**
     *
     * @param  path
     * @return       value or null
     */
    public static Pair<Column, String> getColValueFrom(TreePath path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) path.getLastPathComponent();

        return getColValueFrom(node);
    }

    /**
     *
     * @param  node
     * @return       value or null
     */
    public static Pair<Column,
                       String> getColValueFrom(DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        if (isParentUserObjectAColumnOf(node, XMP_COLUMNS)) {
            String                 value = node.getUserObject().toString();
            DefaultMutableTreeNode parentNode =
                (DefaultMutableTreeNode) node.getParent();
            Column column = (Column) parentNode.getUserObject();

            return new Pair<Column, String>(column, value);
        }

        return null;
    }

    private MiscMetadataHelper() {}
}
