package org.jphototagger.program.module.miscmetadata;

import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.PopupMenuTree;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code PopupMenuTree} as e.g. {@code MiscMetadataPopupMenu} does.
 *
 * @author Elmar Baumann
 */
public final class MiscMetadataPopupMenu extends PopupMenuTree {

    private static final long serialVersionUID = 1L;
    private static final List<MetaDataValue> XMP_META_DATA_VALUES = XmpMetaDataValues.get();
    private JMenuItem itemAddToEditPanel;
    private JMenuItem itemCollapseAllSubitems;
    private JMenuItem itemDelete;
    private JMenuItem itemExpandAllSubitems;
    private JMenuItem itemRemoveFromEditPanel;
    private JMenuItem itemRename;

    public MiscMetadataPopupMenu(JTree tree) {
        super(tree);
        setAccelerators();
        setExpandAllSubItems(itemExpandAllSubitems);
        setCollapseAllSubItems(itemCollapseAllSubitems);
    }

    private void createMenuItems() {
        itemDelete = new JMenuItem(Bundle.getString(MiscMetadataPopupMenu.class, "MiscMetadataPopupMenu.DisplayName.ItemDelete"), AppLookAndFeel.ICON_DELETE);
        itemExpandAllSubitems = new JMenuItem(Bundle.getString(MiscMetadataPopupMenu.class, "MiscMetadataPopupMenu.ItemExpand"));
        itemRename = new JMenuItem(Bundle.getString(MiscMetadataPopupMenu.class, "MiscMetadataPopupMenu.DisplayName.ItemRename"), AppLookAndFeel.ICON_RENAME);
        itemCollapseAllSubitems = new JMenuItem(Bundle.getString(MiscMetadataPopupMenu.class, "MiscMetadataPopupMenu.ItemCollapse"));
        itemAddToEditPanel = new JMenuItem(Bundle.getString(MiscMetadataPopupMenu.class, "MiscMetadataPopupMenu.DisplayName.ActionAddToEditPanel"));
        itemRemoveFromEditPanel = new JMenuItem(Bundle.getString(MiscMetadataPopupMenu.class, "MiscMetadataPopupMenu.DisplayName.ActionRemoveFromEditPanel"));
    }

    @Override
    protected void setMenuItemsEnabled(List<TreePath> selTreePaths) {
        if (selTreePaths == null) {
            throw new NullPointerException("selTreePaths == null");
        }

        boolean xmpValues = allNodesXmpValues(selTreePaths);
        boolean editable = isEditable();

        itemDelete.setEnabled(xmpValues);
        itemRename.setEnabled(xmpValues);
        itemAddToEditPanel.setEnabled(xmpValues && editable);
        itemRemoveFromEditPanel.setEnabled(xmpValues && editable);
    }

    private boolean isEditable() {
        SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

        return editor.isEditable();
    }

    private boolean allNodesXmpValues(List<TreePath> treePaths) {
        for (TreePath treePath : treePaths) {
            if (!isNodeXmpValue(treePath)) {
                return false;
            }
        }

        return true;
    }

    private boolean isNodeXmpValue(TreePath treePath) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();

        return MiscMetadataUtil.isParentUserObjectAMetaDataValue(node, XMP_META_DATA_VALUES);
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemAddToEditPanel() {
        return itemAddToEditPanel;
    }

    public JMenuItem getItemRemoveFromEditPanel() {
        return itemRemoveFromEditPanel;
    }

    @Override
    protected void addMenuItems() {
        createMenuItems();
        add(itemRename);
        add(itemDelete);
        add(new Separator());
        add(itemAddToEditPanel);
        add(itemRemoveFromEditPanel);
        add(new Separator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        addActionsToTree();
    }

    private void addActionsToTree() {
        JTree tree = GUI.getAppPanel().getTreeMiscMetadata();
        InputMap inputMap = tree.getInputMap();
        ActionMap actionMap = tree.getActionMap();
        Action actionRename = itemRename.getAction();
        Action actionDelete = itemDelete.getAction();
        String keyActionRename = "actionRename";
        String keyActionDelete = "actionDelete";

        inputMap.put(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2), keyActionRename);
        actionMap.put(keyActionRename, actionRename);
        inputMap.put(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE), keyActionDelete);
        actionMap.put(keyActionDelete, actionDelete);
    }

    private void setAccelerators() {
        itemDelete.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemAddToEditPanel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_B));
        itemRemoveFromEditPanel.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_BACK_SPACE));
    }
}
