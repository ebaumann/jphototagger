package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.filesystem.MoveFilesController;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to {@code FavoritesPopupMenu#getItemAddFilesystemFolder()} and
 * creates a directory into the file system when the action fires.
 *
 * Also listens to the {@code JTree}'s key events and inserts a new directory
 * into the selected file system directory if the keys <code>Ctrl+N</code> were
 * pressed.
 *
 * @author Elmar Baumann
 */
public final class AddFilesystemFolderToFavoritesController implements ActionListener, KeyListener {

    public AddFilesystemFolderToFavoritesController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemAddFilesystemFolder().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        final JTree tree = GUI.getFavoritesTree();

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N) && !tree.isSelectionEmpty()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
        createDirectory(FavoritesPopupMenu.INSTANCE.getTreePath());
    }

    private void createDirectory(TreePath path) {
        FavoritesTreeModel model = ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class);
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
                MoveFilesController ctrl = ControllerFactory.INSTANCE.getController(MoveFilesController.class);

                if (ctrl != null) {
                    ctrl.moveFiles(selFiles, dir);
                }
            }
        }
    }

    private boolean isMoveSelFiles() {
        String message = Bundle.getString(AddFilesystemFolderToFavoritesController.class, "AddFilesystemFolderToFavoritesController.Confirm.MoveSelFiles");

        return MessageDisplayer.confirmYesNo(null, message);
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
