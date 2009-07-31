package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.EditRepeatableTextEntryPanel;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item
 * {@link HierarchicalKeywordsPanel#getMenuItemRemoveFromEditPanel()}
 * and on action removes the selected hierarchical keyword from the edit
 * panel.  If a keyword was already present it is removed again.
 *
 * Also listens to key events and does the same if Ctrl+D was pressed.
 *
 * @author  Martin Pohlack  <martinp@gmx.de>
 * @version 2009-07-26
 */
public class ControllerRemoveHierarchicalKeywordFromEditPanel
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panelKeywords;

    public ControllerRemoveHierarchicalKeywordFromEditPanel(
            HierarchicalKeywordsPanel panelKeywords) {
        this.panelKeywords = panelKeywords;
        listen();
    }

    private void listen() {
        // Listening to singleton popup menu via ActionListenerFactory#
        // listenToPopupMenuHierarchicalKeywords()
        panelKeywords.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_BACK_SPACE)) {
            removeFromEditPanel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        removeFromEditPanel();
    }

    private void removeFromEditPanel() {
        TreePath path = PopupMenuHierarchicalKeywords.INSTANCE.getTreePath();
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            String keyword = getKeyword((DefaultMutableTreeNode) node);
            if (keyword != null) {
                removeFromEditPanel(keyword);
            }
        }
    }

    private void removeFromEditPanel(String keyword) {
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getEditPanelsArray();
        JPanel panel = editPanels.getEditPanel(
                ColumnXmpDcSubjectsSubject.INSTANCE);
        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel =
                    (EditRepeatableTextEntryPanel) panel;
            if (editPanel.isEditable()) {
                editPanel.removeText(keyword);
            } else {
                MessageDisplayer.error(panelKeywords.getTree(),
                        "ControllerRemoveHierarchicalKeywordFromEditPanel.Error.EditDisabled"); // NOI18N
            }
        } else {
            MessageDisplayer.error(panelKeywords.getTree(),
                    "ControllerRemoveHierarchicalKeywordFromEditPanel.Error.NoEditPanel"); // NOI18N
        }
    }

    private String getKeyword(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            if (keyword.isReal()) {
                return keyword.getKeyword();
            }
        }
        return null;
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
