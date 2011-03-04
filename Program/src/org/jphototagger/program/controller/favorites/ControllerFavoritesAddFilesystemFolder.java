package org.jphototagger.program.controller.favorites;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.filesystem.ControllerMoveFiles;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to {@link PopupMenuFavorites#getItemAddFilesystemFolder()} and
 * creates a directory into the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and inserts a new directory
 * into the selected file system directory if the keys <code>Ctrl+N</code> were
 * pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerFavoritesAddFilesystemFolder implements ActionListener, KeyListener {
    public ControllerFavoritesAddFilesystemFolder() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemAddFilesystemFolder().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        final JTree tree = GUI.getFavoritesTree();

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N) &&!tree.isSelectionEmpty()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Object node = tree.getSelectionPath().getLastPathComponent();

                    if (node instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode) node;

                        createDirectory(new TreePath(pathNode.getPath()));
                    }
                }
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        createDirectory(PopupMenuFavorites.INSTANCE.getTreePath());
    }

    private void createDirectory(TreePath path) {
        TreeModelFavorites model = ModelFactory.INSTANCE.getModel(TreeModelFavorites.class);
        File dir = model.createNewDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(path));

        if (dir != null) {
            confirmMoveSelFilesInto(dir);
        }
    }

    /**
     * Moves all selected files into a directory after confirmation.
     *
     * @param dir directory
     */
    public void confirmMoveSelFilesInto(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        if (dir.isDirectory()) {
            List<File> selFiles = GUI.getSelectedImageFiles();

            if (!selFiles.isEmpty() && isMoveSelFiles()) {
                ControllerMoveFiles ctrl = ControllerFactory.INSTANCE.getController(ControllerMoveFiles.class);

                if (ctrl != null) {
                    ctrl.moveFiles(selFiles, dir);
                }
            }
        }
    }

    private boolean isMoveSelFiles() {
        return MessageDisplayer.confirmYesNo(null, "ControllerFavoritesAddFilesystemFolder.Confirm.MoveSelFiles");
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
