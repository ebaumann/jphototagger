package org.jphototagger.program.module.directories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.lib.model.AllSystemDirectoriesTreeModel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.view.popupmenus.DirectoriesPopupMenu;

/**
 * Listens to {@code DirectoriesPopupMenu#getItemRefresh()} and
 * refreshes the directory tree when the action fires.
 *
 * Also listens to the {@code JTree}'s key events and refreshes the tree
 * when <code>F5</code> was pressed.
 *
 * @author Elmar Baumann
 */
public final class RefreshDirectoryTreeController extends DirectoryController {

    public RefreshDirectoryTreeController() {
        listenToActionsOf(DirectoriesPopupMenu.INSTANCE.getItemRefresh());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F5;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == DirectoriesPopupMenu.INSTANCE.getItemRefresh();
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        ModelFactory.INSTANCE.getModel(AllSystemDirectoriesTreeModel.class).update();
    }
}
