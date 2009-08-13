package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.EditRepeatableTextEntryPanel;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Listens to the menu item
 * {@link HierarchicalKeywordsPanel#getMenuItemAddToEditPanel()}
 * and on action inserts the selected hierarchical keyword and it's real parents
 * into the edit panel.
 *
 * Also listens to key events and does the same if Ctrl+B were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-15
 */
public class ControllerAddHierarchicalKeywordsToEditPanel
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerAddHierarchicalKeywordsToEditPanel(
            HierarchicalKeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_B);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        List<String> keywordNames = new ArrayList<String>();
        addParentKeywords(node, keywordNames);
        addToEditPanel(keywordNames);
    }

    private void addToEditPanel(List<String> keywordNames) {
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getEditPanelsArray();
        JPanel panel = editPanels.getEditPanel(
                ColumnXmpDcSubjectsSubject.INSTANCE);
        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel =
                    (EditRepeatableTextEntryPanel) panel;
            if (editPanel.isEditable()) {
                for (String keywordName : keywordNames) {
                    editPanel.addText(keywordName);
                }
            } else {
                MessageDisplayer.error(getHKPanel().getTree(),
                        "ControllerAddHierarchicalKeywordsToEditPanel.Error.EditDisabled"); // NOI18N
            }
        } else {
            MessageDisplayer.error(getHKPanel().getTree(),
                    "ControllerAddHierarchicalKeywordsToEditPanel.Error.NoEditPanel"); // NOI18N
        }
    }

    private void addParentKeywords(
            DefaultMutableTreeNode node, List<String> keywords) {

        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            if (keyword.isReal()) {
                keywords.add(keyword.getKeyword());
            }
        }
        TreeNode parent = node.getParent();
        if (parent == null ||
                getHKPanel().getTree().getModel().getRoot().equals(parent)) {
            return;
        }
        assert parent instanceof DefaultMutableTreeNode :
                "Not a DefaultMutableTreeNode: " + parent; // NOI18N
        if (parent instanceof DefaultMutableTreeNode) {
            addParentKeywords((DefaultMutableTreeNode) parent, keywords);
        }
    }
}
