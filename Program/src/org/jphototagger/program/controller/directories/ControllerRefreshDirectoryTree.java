package org.jphototagger.program.controller.directories;

import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemRefresh()} and
 * refreshes the directory tree when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and refreshes the tree
 * when <code>F5</code> was pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerRefreshDirectoryTree extends ControllerDirectory {
    public ControllerRefreshDirectoryTree() {
        listenToActionsOf(PopupMenuDirectories.INSTANCE.getItemRefresh());
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

        return evt.getSource() == PopupMenuDirectories.INSTANCE.getItemRefresh();
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        ModelFactory.INSTANCE.getModel(TreeModelAllSystemDirectories.class).update();
    }
}
