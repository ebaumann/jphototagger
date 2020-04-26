package org.jphototagger.lib.io;

import java.awt.Frame;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Creates, renames and deletes directories from the file system and updates
 * trees representing a file system.
 *
 * @author Elmar Baumann
 */
public final class TreeFileSystemDirectories {

    /**
     * Deletes a directory from the file system. Let's the user confirm
     * deletion.
     *
     * @param  directory directory
     * @return           true if deleted
     *
     */
    public static boolean delete(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory()) {
            if (confirmDelete(directory.getName())) {
                try {
                    FileUtil.deleteDirectoryRecursive(directory);

                    return true;
                } catch (Throwable t) {
                    errorMessageDelete(directory.getName());
                    Logger.getLogger(TreeFileSystemDirectories.class.getName()).log(Level.SEVERE, null, t);
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
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory()) {
            String newDirectoryName = getNewName(directory);

            if ((newDirectoryName != null) && !newDirectoryName.trim().isEmpty()) {
                File newDirectory = new File(directory.getParentFile(), newDirectoryName);

                if (checkDoesNotExist(newDirectory)) {
                    try {
                        if (directory.renameTo(newDirectory)) {
                            return newDirectory;
                        }
                    } catch (Throwable t) {
                        Logger.getLogger(TreeFileSystemDirectories.class.getName()).log(Level.SEVERE, null, t);
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
    public static File createDirectoryIn(File parentDirectory) {
        if (parentDirectory == null) {
            throw new NullPointerException("parentDirectory == null");
        }

        if (parentDirectory.isDirectory()) {
            String subdirectoryName = getSubDirectoryName();

            if ((subdirectoryName != null) && !subdirectoryName.trim().isEmpty()) {
                File subdirectory = new File(parentDirectory, subdirectoryName);

                if (checkDoesNotExist(subdirectory)) {
                    try {
                        if (subdirectory.mkdir()) {
                            return subdirectory;
                        }
                    } catch (Throwable t) {
                        Logger.getLogger(TreeFileSystemDirectories.class.getName()).log(Level.SEVERE, null, t);
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
    public static DefaultMutableTreeNode getNodeOfLastPathComponent(TreePath path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

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
     * Updates in descendants of a tree node file user objects after renaming an
     * an ancestor tree node's file user object. E.g. if an ancestor node has a
     * file (directory) named "/1" and it's descendants "/1/2" and "/1/3", after
     * renaming the ancestor into "/2", the file user objects in the descendants
     * are no longer valid. They have to be renamend into "/2/2" and "/2/3".
     *
     * @param renamedNode node, which
     *                    {@link DefaultMutableTreeNode#getUserObject()} is a
     *                    file (directory)
     * @param oldFilepath absolute path name, before the file was renamed
     */
    public static void updateFilesAfterRenamingInTreeModel(final DefaultMutableTreeNode renamedNode, final String oldFilepath) {
        Objects.requireNonNull(renamedNode, "renamedNode == null");
        Objects.requireNonNull(oldFilepath, "oldFilename == null");

        Object userObject = renamedNode.getUserObject();
        if (!(userObject instanceof File)) {
            return;
        }

        final String currentFilePath = ((File) userObject).getAbsolutePath();
        final int oldPathLen = oldFilepath.length();

        TreeUtil.visitTreeNodesRecursive(renamedNode, new TreeUtil.TreeNodeVisitor() {
            @Override
            public void visit(TreeNode node) {
                if (node == renamedNode) {
                    return;
                }
                if (node instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode descendantNode = (DefaultMutableTreeNode) node;
                    Object userObject = descendantNode.getUserObject();
                    if (userObject instanceof File) {
                        String descendantPath = ((File) userObject).getAbsolutePath();
                        if (descendantPath.startsWith(oldFilepath)) {
                            String updatedPath = descendantPath.length() == oldPathLen
                                    ? currentFilePath
                                    : currentFilePath + descendantPath.substring(oldPathLen);
                            descendantNode.setUserObject(new File(updatedPath));
                        }
                    }
                }
            }
        });
    }

    /**
     * Inserts into a model a file if the model has the type
     * {@code DefaultTreeModel}.
     *
     * @param model      model
     * @param parentNode parent node
     * @param file       file to insert as user object in a
     *                   <code>DefaultMutableTreeNode</code> as the user object
     *                   of a {@code DefaultMutableTreeNode}
     */
    public static void insertIntoTreeModel(TreeModel model, DefaultMutableTreeNode parentNode, File file) {
        if (parentNode == null) {
            throw new NullPointerException("parentNode == null");
        }

        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (model instanceof DefaultTreeModel) {
            ((DefaultTreeModel) model).insertNodeInto(new DefaultMutableTreeNode(file), parentNode,
                    parentNode.getChildCount());
        }
    }

    /**
     * Deletes from a model a tree node if the model's type is
     * {@code DefaultTreeModel} and all it's nodes of the type
     * {@code MutableTreeNode}.
     *
     * @param model model
     * @param node  node to delete
     */
    public static void removeFromTreeModel(TreeModel model, MutableTreeNode node) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        if (node == null) {
            throw new NullPointerException("node == null");
        }

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
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        Frame parentFrame = ComponentUtil.findFrameWithIcon();
        String message = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Input.NewName", file);
        String initSelectionValue = file.getName();

        return JOptionPane.showInputDialog(parentFrame, message, initSelectionValue);
    }

    /**
     * Checks whether a file does not exist. If exists, this method
     * shows an error message.
     *
     * @param  file file
     * @return true if the file does <em>not</em> exist
     */
    public static boolean checkDoesNotExist(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (file.exists()) {
            Frame parentFrame = ComponentUtil.findFrameWithIcon();
            String message = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Error.DirectoryAlreadyExists", file.getAbsolutePath());
            String title = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Error.DirectoryAlreadyExists.Title");

            JOptionPane.showMessageDialog(parentFrame, message, title, JOptionPane.ERROR_MESSAGE);

            return false;
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
        if (directoryName == null) {
            throw new NullPointerException("directoryName == null");
        }

        Frame parentFrame = ComponentUtil.findFrameWithIcon();
        String message = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Confirm.Delete", directoryName);
        String title = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Confirm.Delete.Title");
        int optionType = JOptionPane.YES_NO_OPTION;

        return JOptionPane.showConfirmDialog(parentFrame, message, title, optionType) == JOptionPane.YES_OPTION;
    }

    /**
     * Shows an error message dialog that a specific directory couldn't be
     * deleted.
     *
     * @param directoryName name of the directory
     */
    public static void errorMessageDelete(String directoryName) {
        if (directoryName == null) {
            throw new NullPointerException("directoryName == null");
        }

        Frame parentFrame = ComponentUtil.findFrameWithIcon();
        String message = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Error.Delete", directoryName);
        String titel = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Error.Delete.Title");
        int messageType = JOptionPane.ERROR_MESSAGE;

        JOptionPane.showMessageDialog(parentFrame, message, titel, messageType);
    }

    private static String getSubDirectoryName() {
        Frame parentFrame = ComponentUtil.findFrameWithIcon();
        String message = Bundle.getString(TreeFileSystemDirectories.class, "TreeFileSystemDirectories.Input.SubDirectoryName");

        return JOptionPane.showInputDialog(parentFrame, message);
    }

    private TreeFileSystemDirectories() {
    }
}
