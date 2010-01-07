/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.HierarchicalKeywordTreeNodesClipboard.Action;
import de.elmar_baumann.jpt.datatransfer.Flavors;
import de.elmar_baumann.jpt.datatransfer.TransferHandlerTreeHierarchicalKeywords;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

// The implementation can't paste the nodes to the system clipboard and let do
// the work the panel's transfer handler, because the affected nodes when using
// the popup menu may not be the selected nodes as expected by the panel's
// transfer handler.
/**
 * Listens to the menu items {@link PopupMenuHierarchicalKeywords#getMenuItemCut()},
 * {@link PopupMenuHierarchicalKeywords#getMenuItemPaste()} and on action
 * cuts a keyword to the clipboard or pastes a cutted item.
 *
 * The key action Ctrl+X and Ctrl+V are handled by the JTree.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-10
 */
public class ControllerCopyCutPasteHierarchicalKeyword
        implements ActionListener, KeyListener {

    private final KeywordsPanel     panel;
    private final PopupMenuHierarchicalKeywords popup     = PopupMenuHierarchicalKeywords.INSTANCE;
    private final JMenuItem                     itemCopy  = popup.getMenuItemCopy();
    private final JMenuItem                     itemCut   = popup.getMenuItemCut();
    private final JMenuItem                     itemPaste = popup.getMenuItemPaste();

    public ControllerCopyCutPasteHierarchicalKeyword(KeywordsPanel panel) {
        this.panel = panel;
    }

    // Do not extend ControllerHierarchicalKeywords and using localAction
    // because listening to 2 actions: cut is only 1 line of code - too less
    // to implement a separate class

    @Override
    public void actionPerformed(ActionEvent e) {
        Object                 source            = e.getSource();
        Object                 lastPathComponent = popup.getTreePath().getLastPathComponent();
        DefaultMutableTreeNode node              = (DefaultMutableTreeNode) lastPathComponent;

        if (source == itemCut) {
            HierarchicalKeywordTreeNodesClipboard.INSTANCE.setContent(node, Action.MOVE);
        } else if (source == itemCopy) {
            HierarchicalKeywordTreeNodesClipboard.INSTANCE.setContent(node, Action.COPY);
        } else if (source == itemPaste) {
            pasteMenuAction(node);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        assert e.getSource() instanceof JTree : e.getSource();

        JTree tree = (JTree)e.getSource();

        if (tree.isSelectionEmpty()) return;

        if (KeyEventUtil.isCopy(e)) {
            HierarchicalKeywordTreeNodesClipboard.INSTANCE.setContent(getFirstSelectedNode(tree), Action.COPY);
        } else if (KeyEventUtil.isCut(e)) {
            HierarchicalKeywordTreeNodesClipboard.INSTANCE.setContent(getFirstSelectedNode(tree), Action.MOVE);
        } else if (isPasteCopyFromClipBoard(e)) {
            pasteCopy(tree);
        }
    }

    private DefaultMutableTreeNode getFirstSelectedNode(JTree tree) {

        assert !tree.isSelectionEmpty();

        return (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
    }

    private void pasteMenuAction(DefaultMutableTreeNode node) {
        if (isMoveFromClipBoard()) {
            move(node);
        } else if (isCopyFromClipBoard()) {
            pasteCopy(popup.getTree());
        }
    }

    private void move(DefaultMutableTreeNode node) {
        Transferable trans = new TransferableObject(
                new ArrayList<DefaultMutableTreeNode>(
                    HierarchicalKeywordTreeNodesClipboard.INSTANCE.getContent()),
                Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);

        TransferHandlerTreeHierarchicalKeywords.moveKeywords(
                new TransferSupport(panel, trans),
                (TreeModelHierarchicalKeywords) panel.getTree().getModel(),
                node);

        HierarchicalKeywordTreeNodesClipboard.INSTANCE.empty();
    }

    private void pasteCopy(JTree tree) {
        TreePath[] selPaths = tree.getSelectionPaths();

        assert selPaths != null;

        if (!ensureCopyToAll(selPaths.length)) return;

        TreeModelHierarchicalKeywords model = (TreeModelHierarchicalKeywords) panel.getTree().getModel();

        for (DefaultMutableTreeNode node : HierarchicalKeywordTreeNodesClipboard.INSTANCE.getContent()) {
            for (TreePath selPath : selPaths) {
                model.copySubtree(node,
                        (DefaultMutableTreeNode) selPath.getLastPathComponent());
            }
        }

        HierarchicalKeywordTreeNodesClipboard.INSTANCE.empty();
    }

    private boolean ensureCopyToAll(int count) {
        if (count < 1 ) return false;
        if (count == 1) return true;
        return MessageDisplayer.confirmYesNo(null,
                "ControllerCopyCutPasteHierarchicalKeyword.Confirm.CopyToAllSelected");
    }

    private boolean isCopyFromClipBoard() {
        return !HierarchicalKeywordTreeNodesClipboard.INSTANCE.isEmpty() &&
                HierarchicalKeywordTreeNodesClipboard.INSTANCE.isCopy();
    }

    private boolean isMoveFromClipBoard() {
        return  !HierarchicalKeywordTreeNodesClipboard.INSTANCE.isEmpty() &&
                 HierarchicalKeywordTreeNodesClipboard.INSTANCE.isMove();
    }

    private boolean isPasteCopyFromClipBoard(KeyEvent e) {
        return    !HierarchicalKeywordTreeNodesClipboard.INSTANCE.isEmpty()
                && KeyEventUtil.isPaste(e)
                && HierarchicalKeywordTreeNodesClipboard.INSTANCE.isCopy();
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
