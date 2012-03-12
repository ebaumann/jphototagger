package org.jphototagger.lib.swing;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * Tree model for all directories of a file system. All nodes have the type
 * {@code DefaultMutableTreeNode} and their user object is a file with exception of the root directory.
 *
 * @author Elmar Baumann
 */
public final class AllSystemDirectoriesTreeModel extends DefaultTreeModel implements TreeWillExpandListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName());
    private static final long UPDATE_INTERVAL_MILLISECONDS = 2000;
    private final DirectoryFilter directoryFilter;
    private final DefaultMutableTreeNode rootNode;
    private final JTree tree;
    private final List<File> excludeRootDirectories = new ArrayList<File>();
    private final ScheduledExecutorService updateScheduler;
    private volatile boolean autoupdate;

    public AllSystemDirectoriesTreeModel(JTree tree, Collection<? extends File> excludeRootDirectories,
            DirectoryFilter.Option... directoryFilterOption) {
        super(new SortedChildrenTreeNode("Root of TreeModelAllSystemDirectories"));
        rootNode = (DefaultMutableTreeNode) getRoot();
        this.tree = tree;
        this.directoryFilter = new DirectoryFilter(directoryFilterOption);
        this.excludeRootDirectories.addAll(excludeRootDirectories);
        addRootDirectories();
        listen(tree);
        updateScheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    private void listen(JTree tree) {
        tree.addTreeWillExpandListener(this);
    }

    private void addRootDirectories() {
        LOGGER.log(Level.FINEST, "Reading root directories...");
        File[] roots = File.listRoots();
        LOGGER.log(Level.FINEST, "Root directories have been read: {0}", StringUtil.toString(roots));
        LOGGER.log(Level.FINEST, "Root directories to exclude: {0}: ", excludeRootDirectories);
        if (roots == null) {
            return;
        }
        List<File> rootDirectories = Arrays.asList(roots);
        for (File rootDirectory : rootDirectories) {
            boolean isExclude = excludeRootDirectories.contains(rootDirectory);
            if (!isExclude) {
                DefaultMutableTreeNode rootDirectoryNode = new SortedChildrenTreeNode(rootDirectory);
                insertNodeInto(rootDirectoryNode, rootNode, rootNode.getChildCount());
                addChildren(rootDirectoryNode);
            }
        }
    }

    private void addChildren(DefaultMutableTreeNode parentNode) {
        Object userObject = parentNode.getUserObject();
        File parentDirectory = userObject instanceof File
                ? (File) userObject
                : null;
        if ((parentDirectory == null) || !parentDirectory.isDirectory()) {
            return;
        }
        LOGGER.log(Level.FINEST, "Reading subdirectories of ''{0}''...", parentDirectory);
        File[] subdirectories = parentDirectory.listFiles(directoryFilter);
        LOGGER.log(Level.FINEST, "Subdirectories of ''{0}'' has been read: {1}",
                new Object[]{parentDirectory, StringUtil.toString(subdirectories)});
        if (subdirectories == null) {
            return;
        }
        List<File> parentNodeChildDirectories = getChildDirectories(parentNode);
        for (File subdirectory : subdirectories) {
            if (!parentNodeChildDirectories.contains(subdirectory)) {
                addChildDirectory(parentNode, subdirectory);
            }
        }
    }

    private static List<File> getChildDirectories(DefaultMutableTreeNode node) {
        int childCount = node.getChildCount();
        List<File> childFiles = new ArrayList<File>(childCount);
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            Object childNodeUserObject = childNode.getUserObject();
            if (childNodeUserObject instanceof File) {
                childFiles.add((File) childNodeUserObject);
            }
        }
        return childFiles;
    }

    private void addChildDirectory(DefaultMutableTreeNode parentNode, File directory) {
        LOGGER.log(Level.FINEST, "Adding subdirectory ''{0}'' to node ''{1}''",
                new Object[]{directory, parentNode});
        DefaultMutableTreeNode newChildNode = new SortedChildrenTreeNode(directory);
        parentNode.add(newChildNode);
        int newChildIndex = parentNode.getIndex(newChildNode);
        fireTreeNodesInserted(this, parentNode.getPath(), new int[]{newChildIndex}, new Object[]{newChildNode});
        LOGGER.log(Level.FINEST, "Subdirectory ''{0}'' has been added to node ''{1}''",
                new Object[]{directory, parentNode});
    }

    private int removeChildrenWithNotExistingFiles(DefaultMutableTreeNode parentNode) {
        int childCount = parentNode.getChildCount();
        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<DefaultMutableTreeNode>();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object userObject = child.getUserObject();
            File file = null;
            if (userObject instanceof File) {
                file = (File) userObject;
            }
            if ((file != null) && !file.exists()) {
                nodesToRemove.add(child);
            }
        }
        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            LOGGER.log(Level.FINEST, "Removing child node ''{0}'' from parent node ''{1}''...",
                    new Object[]{childNodeToRemove, parentNode});
            removeNodeFromParent(childNodeToRemove);
            LOGGER.log(Level.FINEST, "Child node ''{0}'' has been removed from parent node ''{1}''",
                    new Object[]{childNodeToRemove, parentNode});
        }
        return nodesToRemove.size();
    }

    /**
     * Creates a new directory as child of a node. Let's the user input the new name and inserts the new created
     * directory.
     *
     * @param parentNode parent node. If null, nothing will be done.
     * @return created directory or null if not created
     */
    public File createDirectoryIn(DefaultMutableTreeNode parentNode) {
        File parentNodeDirectory = (parentNode == null)
                ? null
                : TreeFileSystemDirectories.getFile(parentNode);
        if (parentNodeDirectory != null) {
            File createdDirectory = TreeFileSystemDirectories.createDirectoryIn(parentNodeDirectory);
            if (createdDirectory != null) {
                SortedChildrenTreeNode createdDirNode = new SortedChildrenTreeNode(createdDirectory);
                parentNode.add(createdDirNode);
                int childIndex = parentNode.getIndex(createdDirNode);
                fireTreeNodesInserted(this, parentNode.getPath(), new int[]{childIndex}, new Object[]{createdDirNode});
                return createdDirectory;
            }
        }
        return null;
    }

    /**
     * Expands the tree to a specific file.
     *
     * @param file file
     * @param select if true the file node will be selected
     */
    public void expandToFile(File file, boolean select) {
        Stack<File> filePath = FileUtil.getPathFromRoot(file);
        DefaultMutableTreeNode node = rootNode;
        while ((node != null) && !filePath.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(node, filePath.pop());
            if ((node != null) && (node.getChildCount() <= 0)) {
                addChildren(node);
            }
        }
        if ((node != null) && (node.getParent() != null)) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent.getPath() != null) {
                tree.expandPath(new TreePath(parent.getPath()));
                if (select) {
                    TreePath path = new TreePath(node.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            }
        }
    }

    /**
     * Updates this model: Adds nodes for new files, deletes nodes with not existing files.
     */
    public void update() {
        Cursor treeCursor = tree.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        tree.setCursor(waitCursor);
        for (DefaultMutableTreeNode node : getTreeRowNodes()) {
            addChildren(node);
            // Ensure that added nodes aware of it's children (else JTree doesn't show opening handles)
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                Object child = e.nextElement();
                if (child instanceof DefaultMutableTreeNode) {
                    addChildren((DefaultMutableTreeNode) child);
                }
            }
            removeChildrenWithNotExistingFiles(node);
        }
        tree.setCursor(treeCursor);
    }

    private List<DefaultMutableTreeNode> getTreeRowNodes() {
        int rowCount = tree.getRowCount();
        List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            nodes.add((DefaultMutableTreeNode) tree.getPathForRow(i).getLastPathComponent());
        }
        return nodes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        Cursor treeCursor = tree.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        tree.setCursor(waitCursor);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        LOGGER.log(Level.FINEST, "Node ''{0}'' has been expanded, adding children...", node);
        if (node.getChildCount() == 0) {
            addChildren(node);
        }
        List<DefaultMutableTreeNode> children = TreeUtil.getDefaultMutableTreeNodeChildren(node);
        for (DefaultMutableTreeNode child : children) {
            addChildren(child);
        }
        LOGGER.log(Level.FINEST, "Children were added to node ''{0}'' after expanding", node);
        tree.setCursor(treeCursor);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // ignore
    }

    /**
     * Scans external filesystem for changes. Default: false.
     */
    public void startAutoUpdate() {
        synchronized (updateScheduler) {
            if (!autoupdate) {
                updateScheduler.scheduleWithFixedDelay(updater, 0, UPDATE_INTERVAL_MILLISECONDS, TimeUnit.MILLISECONDS);
                autoupdate = true;
            }
        }
    }

    /**
     * Stops scan for external filesystem for changes if started.
     */
    public void stopAutoUpdate() {
        synchronized (updateScheduler) {
            if (autoupdate) {
                updateScheduler.shutdown();
                autoupdate = false;
            }
        }
    }

    private final Runnable updater = new Runnable() {

        private AtomicInteger taskCount = new AtomicInteger(0);

        @Override
        public void run() {
            if (taskCount.intValue() > 0) {
                return;
            }
            addNewRoots();
            update(getTreeRowNodes());
        }

        private void update(Collection<? extends DefaultMutableTreeNode> nodes) {
            for (DefaultMutableTreeNode node : nodes) {
                Object userObject = node.getUserObject();
                if (userObject instanceof File) {
                    File dir = (File) userObject;
                    if (!dir.exists()) {
                        remove(node);
                    } else {
                        addNewChildren(node);
                    }
                }
            }
        }

        private void addNewRoots() {
            taskCount.incrementAndGet();
            final File[] rootFiles = File.listRoots();
            if (rootFiles == null) {
                taskCount.decrementAndGet();
                return;
            }
            final List<File> childDirectories = getChildDirectories(rootNode);
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    for (File rootFile : rootFiles) {
                        if (rootFile.isDirectory() && !childDirectories.contains(rootFile)) {
                            addChildDirectory(rootNode, rootFile);
                        }
                    }
                    taskCount.decrementAndGet();
                }
            });
        }

        private void remove(final DefaultMutableTreeNode node) {
            taskCount.incrementAndGet();
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    removeNodeFromParent(node);
                    taskCount.decrementAndGet();
                }
            });
        }

        private void addNewChildren(final DefaultMutableTreeNode node) {
            taskCount.incrementAndGet();
            File dir = (File) node.getUserObject();
            final List<File> childDirectories = getChildDirectories(node);
            final List<File> directories = FileUtil.listFiles(dir, directoryFilter);
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    for (File directory : directories) {
                        if (directory.isDirectory() && !childDirectories.contains(directory)) {
                            addChildDirectory(node, directory);
                        }
                    }
                    taskCount.decrementAndGet();
                }
            });
        }
    };

    private final ThreadFactory threadFactory = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("JPhotoTagger: System Directories Tree Update");
            return thread;
        }
    };
}
