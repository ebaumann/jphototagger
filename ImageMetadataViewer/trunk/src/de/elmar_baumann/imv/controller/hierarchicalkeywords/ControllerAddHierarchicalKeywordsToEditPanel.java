package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.EditRepeatableTextEntryPanel;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item
 * {@link HierarchicalKeywordsPanel#getMenuItemAddToEditPanel()}
 * and on action inserts the selected hierarchical keyword and it's real parents
 * into the edit panel.
 *
 * Also listens to key events and does the same if Ctrl+B were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/15
 */
public class ControllerAddHierarchicalKeywordsToEditPanel
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panelKeywords =
            HierarchicalKeywordsDialog.INSTANCE.getPanel();
    private final EditMetadataPanelsArray editPanels =
            GUI.INSTANCE.getAppPanel().getEditPanelsArray();

    public ControllerAddHierarchicalKeywordsToEditPanel() {
        listen();
    }

    private void listen() {
        panelKeywords.getMenuItemAddToEditPanel().addActionListener(this);
        panelKeywords.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_B)) {
            addToEditPanel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addToEditPanel();
    }

    private void addToEditPanel() {
        JTree tree = panelKeywords.getTree();
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            MessageDisplayer.error(
                    "ControllerAddHierarchicalKeywordsToEditPanel.Error.NoPathSelected"); // NOI18N
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                List<String> keywordNames = new ArrayList<String>();
                addParentKeywords((DefaultMutableTreeNode) node, keywordNames);
                addToEditPanel(keywordNames);
            }
        }
    }

    private void addToEditPanel(List<String> keywordNames) {
        JPanel panel = editPanels.getEditPanel(
                ColumnXmpDcSubjectsSubject.INSTANCE);
        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel =
                    (EditRepeatableTextEntryPanel) panel;
            String text = ArrayUtil.toTokenString(keywordNames,
                    XmpMetadata.getXmpTokenDelimiter(), "?").trim();
            if (editPanel.isEditable()) {
                if (!text.isEmpty()) {
                    editPanel.addText(text);
                }
            } else {
                MessageDisplayer.error(
                        "ControllerAddHierarchicalKeywordsToEditPanel.Error.EditDisabled");
            }
        } else {
            MessageDisplayer.error(
                    "ControllerAddHierarchicalKeywordsToEditPanel.Error.NoEditPanel");
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
                panelKeywords.getTree().getModel().getRoot().equals(parent)) {
            return;
        }
        assert parent instanceof DefaultMutableTreeNode : parent;
        if (parent instanceof DefaultMutableTreeNode) {
            addParentKeywords((DefaultMutableTreeNode) parent, keywords);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
