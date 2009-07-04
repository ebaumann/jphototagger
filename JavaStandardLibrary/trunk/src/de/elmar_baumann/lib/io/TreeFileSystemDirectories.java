package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Creates, renames and deletes directories from the file system and updates
 * trees representing a file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/29
 */
public final class TreeFileSystemDirectories {

    /**
     * Deletes a directory from the file system. Let's the user confirm deletion.
     *
     * @param  directory directory
     * @return           true if deleted and false if not deleted or the file
     *                   isn't a directory
     *
     */
    public static boolean delete(File directory) {
        if (directory.isDirectory()) {
            if (confirmDelete(directory.getName())) {
                try {
                    if (FileUtil.deleteDirectory(directory)) {
                        return true;
                    } else {
                        errorMessageDelete(directory.getName());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(
                            TreeFileSystemDirectories.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    /**
     * Renames a directory into the file system. Let's the user input the new
     * name.
     *
     * @param  directory directory
     * @return           new file or null if not renamed
     *
     */
    public static File rename(File directory) {
        if (directory.isDirectory()) {
            String newDirectoryName = getNewName(directory);
            if (newDirectoryName != null &&
                    !newDirectoryName.trim().isEmpty()) {
                File newDirectory = new File(directory.getParentFile(),
                        newDirectoryName);
                if (checkDoesNotExist(newDirectory)) {
                    try {
                        if (directory.renameTo(newDirectory)) {
                            return newDirectory;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(
                                TreeFileSystemDirectories.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Creates a new subdirectory. Asks the user for the name.
     *
     * @param  parentDirectory parent directory into which the new directory
     *                         will be created
     * @return                 created subdirectory or null if not created
     */
    public static File createSubDirectory(File parentDirectory) {
        if (parentDirectory.isDirectory()) {
            String subdirectoryName = getSubDirectoryName();
            if (subdirectoryName != null &&
                    !subdirectoryName.trim().isEmpty()) {
                File subdirectory = new File(parentDirectory, subdirectoryName);
                if (checkDoesNotExist(subdirectory)) {
                    try {
                        if (subdirectory.mkdir()) {
                            return subdirectory;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(
                                TreeFileSystemDirectories.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the node of the last component of a path.
     *
     * @param  path path
     * @return      node or null if the type of the last path component has not
     *              the type <code>DefaultMutableTreeNode</code>
     */
    public static DefaultMutableTreeNode getNodeOfLastPathComponent(
            TreePath path) {
        Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) lastPathComponent;
        }
        return null;
    }

    /**
     * Returns a file when the user object of a node is a file.
     *
     * @param  node node can be null
     * @return      file or null if the user object is not a file or node is
     *              null
     */
    public static File getFile(DefaultMutableTreeNode node) {
        if (node != null) {
            Object userObject = node.getUserObject();
            if (userObject instanceof File) {
                return (File) userObject;
            }
        }
        return null;
    }

    /**
     * Updates a node into a model if the model has the type
     * {@link DefaultTreeModel}.
     *
     * @param model  model
     * @param node   updated node
     */
    public static void updateInTreeModel(TreeModel model, MutableTreeNode node) {
        if (model instanceof DefaultTreeModel) {
            ((DefaultTreeModel) model).nodeChanged(node);
        }
    }

    /**
     * Inserts into a model a file if the model has the type
     * {@link DefaultTreeModel}.
     * 
     * @param model      model
     * @param parentNode parent node
     * @param file       file to insert as user object in a
     *                   <code>DefaultMutableTreeNode</code> as the user object
     *                   of a {@link DefaultMutableTreeNode}
     */
    public static void insertIntoTreeModel(
            TreeModel model, DefaultMutableTreeNode parentNode, File file) {
        if (model instanceof DefaultTreeModel) {
            ((DefaultTreeModel) model).insertNodeInto(new DefaultMutableTreeNode(
                    file), parentNode, parentNode.getChildCount());
        }
    }

    /**
     * Deletes from a model a tree node if the model's type is
     * {@link DefaultTreeModel} and all it's nodes of the type
     * {@link MutableTreeNode}.
     *
     * @param model model
     * @param node  node to delete
     */
    public static void removeFromTreeModel(TreeModel model, MutableTreeNode node) {
        if (model instanceof DefaultTreeModel) {
            ((DefaultTreeModel) model).removeNodeFromParent(node);
        }
    }

    /**
     * Let's the user input a new name for a file.
     *
     * @param  file file to rename
     * @return      new name or null if the user didn't input a name
     */
    public static String getNewName(File file) {
        return JOptionPane.showInputDialog(null, Bundle.getString(
                "TreeFileSystemDirectories.Input.NewName", file), file.getName());
    }

    /**
     * Checks whether a file does not exist. If exists, this method
     * shows an error message.
     *
     * @param  file file
     * @return true if the file does <em>not</em> exist
     */
    public static boolean checkDoesNotExist(File file) {
        if (file.exists()) {
            JOptionPane.showMessageDialog(null,
                    Bundle.getString(
                    "TreeFileSystemDirectories.ErrorMessage.DirectoryAlreadyExists",
                    file.getAbsolutePath()),
                    Bundle.getString(
                    "TreeFileSystemDirectories.ErrorMessage.DirectoryAlreadyExists.Title"),
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    /**
     * Asks the user wether to delete a directory.
     *
     * @param  directoryName name of the directory
     * @return true if the directory shall be deleted
     */
    public static boolean confirmDelete(String directoryName) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString(
                "TreeFileSystemDirectories.ConfirmMessage.Delete",
                directoryName),
                Bundle.getString(
                "TreeFileSystemDirectories.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION) ==
                JOptionPane.YES_OPTION;
    }

    /**
     * Shows an error message dialog that a specific directory couldn't be
     * deleted.
     *
     * @param directoryName name of the directory
     */
    public static void errorMessageDelete(String directoryName) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString("TreeFileSystemDirectories.ErrorMessage.Delete",
                directoryName),
                Bundle.getString(
                "TreeFileSystemDirectories.ErrorMessage.Delete.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static String getSubDirectoryName() {
        return JOptionPane.showInputDialog(null, Bundle.getString(
                "TreeFileSystemDirectories.Input.SubDirectoryName"));
    }

    private TreeFileSystemDirectories() {
    }
}
