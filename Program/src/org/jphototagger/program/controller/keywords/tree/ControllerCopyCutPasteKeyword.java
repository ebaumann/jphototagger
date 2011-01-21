package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.keywords.tree
    .KeywordTreeNodesClipboard.Action;
import org.jphototagger.program.datatransfer.Flavor;
import org.jphototagger.program.datatransfer.TransferHandlerKeywordsTree;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

//The implementation can't paste the nodes to the system clipboard and let do
//the work the panel's transfer handler, because the affected nodes when using
//the popup menu may not be the selected nodes as expected by the panel's
//transfer handler.

/**
 * Listens to the menu items {@link PopupMenuKeywordsTree#getItemCut()},
 * {@link PopupMenuKeywordsTree#getItemPaste()} and on action
 * cuts a keyword to the clipboard or pastes a cutted item.
 *
 * The key action Ctrl+X and Ctrl+V are handled by the JTree.
 *
 * @author Elmar Baumann
 */
public class ControllerCopyCutPasteKeyword
        implements ActionListener, KeyListener {
    private final KeywordsPanel panel;

    public ControllerCopyCutPasteKeyword(KeywordsPanel panel) {
        if (panel == null) {
            throw new NullPointerException("panel == null");
        }

        this.panel = panel;
        listen();
    }

    private void listen() {
        panel.addKeyListener(this);
    }

    private JMenuItem getCopyItem() {
        return PopupMenuKeywordsTree.INSTANCE.getItemCopy();
    }

    private JMenuItem getCutItem() {
        return PopupMenuKeywordsTree.INSTANCE.getItemCut();
    }

    private JMenuItem getPasteItem() {
        return PopupMenuKeywordsTree.INSTANCE.getItemPaste();
    }

    // Does not extend ControllerKeywords and using localAction
    // because listening to 2 actions: cut is only 1 line of code - too less
    // to implement a separate class
    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        Object lastPathComponent =
            PopupMenuKeywordsTree.INSTANCE.getTreePath().getLastPathComponent();
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) lastPathComponent;

        if (source == getCutItem()) {
            KeywordTreeNodesClipboard.INSTANCE.setContent(node, Action.MOVE);
        } else if (source == getCopyItem()) {
            KeywordTreeNodesClipboard.INSTANCE.setContent(node, Action.COPY);
        } else if (source == getPasteItem()) {
            pasteMenuAction(node);
        }
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        assert evt.getSource() instanceof JTree : evt.getSource();

        JTree tree = (JTree) evt.getSource();

        if (tree.isSelectionEmpty()) {
            return;
        }

        if (KeyEventUtil.isCopy(evt)) {
            KeywordTreeNodesClipboard.INSTANCE.setContent(
                getFirstSelectedNode(tree), Action.COPY);
        } else if (KeyEventUtil.isCut(evt)) {
            KeywordTreeNodesClipboard.INSTANCE.setContent(
                getFirstSelectedNode(tree), Action.MOVE);
        } else if (isPasteCopyFromClipBoard(evt)) {
            pasteCopy(tree);
        }
    }

    private DefaultMutableTreeNode getFirstSelectedNode(JTree tree) {
        assert !tree.isSelectionEmpty();

        return (DefaultMutableTreeNode) tree.getSelectionPath()
            .getLastPathComponent();
    }

    private void pasteMenuAction(DefaultMutableTreeNode node) {
        if (isMoveFromClipBoard()) {
            move(node);
        } else if (isCopyFromClipBoard()) {
            pasteCopy(PopupMenuKeywordsTree.INSTANCE.getTree());
        }
    }

    private void move(DefaultMutableTreeNode node) {
        Transferable trans =
            new TransferableObject(
                new ArrayList<DefaultMutableTreeNode>(
                    KeywordTreeNodesClipboard.INSTANCE.getContent()), Flavor
                        .KEYWORDS_TREE);

        TransferHandlerKeywordsTree.moveKeywords(
            new TransferSupport(panel, trans),
            ModelFactory.INSTANCE.getModel(TreeModelKeywords.class), node);
        KeywordTreeNodesClipboard.INSTANCE.empty();
    }

    private void pasteCopy(final JTree tree) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreePath[] selPaths = tree.getSelectionPaths();

                assert selPaths != null;

                if (!ensureCopyToAll(selPaths.length)) {
                    return;
                }

                TreeModelKeywords model =
                    ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

                for (DefaultMutableTreeNode node :
                        KeywordTreeNodesClipboard.INSTANCE.getContent()) {
                    for (TreePath selPath : selPaths) {
                        model.copySubtree(node,
                                          (DefaultMutableTreeNode) selPath
                                              .getLastPathComponent());
                    }
                }

                KeywordTreeNodesClipboard.INSTANCE.empty();
            }
        });
    }

    private boolean ensureCopyToAll(int count) {
        if (count < 1) {
            return false;
        }

        if (count == 1) {
            return true;
        }

        return MessageDisplayer.confirmYesNo(null,
                "ControllerCopyCutPasteKeyword.Confirm.CopyToAllSelected");
    }

    private boolean isCopyFromClipBoard() {
        return !KeywordTreeNodesClipboard.INSTANCE.isEmpty()
               && KeywordTreeNodesClipboard.INSTANCE.isCopy();
    }

    private boolean isMoveFromClipBoard() {
        return !KeywordTreeNodesClipboard.INSTANCE.isEmpty()
               && KeywordTreeNodesClipboard.INSTANCE.isMove();
    }

    private boolean isPasteCopyFromClipBoard(KeyEvent evt) {
        return !KeywordTreeNodesClipboard.INSTANCE.isEmpty()
               && KeyEventUtil.isPaste(evt)
               && KeywordTreeNodesClipboard.INSTANCE.isCopy();
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }
}
