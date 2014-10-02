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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.StringUtil;

/* Speed improvements (why JTree is required):
 * * Child nodes will be added only, when a node will be expanded in the JTree,
 *   e.g. due a click onto an "expand" node handle (many UIs renders this handle
 *   as a "plus" icon)
 * * Because the model doesn't have a method such as "hasChildren()", the UI uses
 *   "getChildCount()", to determine, whether to render an "expand" node handle. To
 *   avoid adding all child directories to each node, to get a child count greater
 *   than zero, a dummy node will be added to each collapsed node, if the directory
 *   is not empty. When expanding that node, the dummy child node will be replaced
 *   with true directory nodes (these will have dummy nodes until expansion).
 */

/*
   Code to retrieve Network Environment under Windows (not sure, whether this works
   on other machines):

    FileSystemView fsv = FileSystemView.getFileSystemView();
    File homeDirectory = fsv.getHomeDirectory();
    File[] homeDirectoryFiles = homeDirectory.listFiles();
    for (File homeDirectoryFile : homeDirectoryFiles) {
        if (homeDirectoryFile.getName().startsWith("::")) {
            File[] filesInColons = homeDirectoryFile.listFiles();
            for (File file : filesInColons) {
                if (file.getAbsolutePath().startsWith("\\\\")) {
                    File[] networkFile = file.listFiles();
                    // Add this to a network node
                }
            }
        }
    }
*/

/**
 * Tree model for all directories of a file system. All nodes are {@code DefaultMutableTreeNode}s and their user objects
 * are files with exception of the root directory.
 *
 * @author Elmar Baumann
 */
public final class AllSystemDirectoriesTreeModel extends DefaultTreeModel implements TreeWillExpandListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName());
    private static final long UPDATE_INTERVAL_MILLISECONDS = 2000;
    private static final Object DUMMY = new Object();
    private final DirectoryFilter directoryFilter;
    private final DefaultMutableTreeNode rootNode;
    private final JTree tree;
    private final List<File> excludedRootDirs = new ArrayList<>();
    private final ScheduledExecutorService updateScheduler;
    private volatile boolean autoupdate;

    public AllSystemDirectoriesTreeModel(JTree tree, Collection<? extends File> excludedRootDirectories,
            DirectoryFilter.Option... directoryFilterOptions) {
        super(new SortedChildrenTreeNode("Root of TreeModelAllSystemDirectories"));
        rootNode = (DefaultMutableTreeNode) getRoot();
        this.tree = tree;
        this.directoryFilter = new DirectoryFilter(directoryFilterOptions);
        this.excludedRootDirs.addAll(excludedRootDirectories);
        addRootDirectories();
        listen();
        updateScheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    private void listen() {
        tree.addTreeWillExpandListener(this);
    }

    private void addRootDirectories() {
        LOGGER.log(Level.FINEST, "Reading root directories...");
        File[] roots = File.listRoots();
        LOGGER.log(Level.FINEST, "Root directories have been read: {0}", StringUtil.toString(roots));
        LOGGER.log(Level.FINEST, "Root directories to exclude: {0}: ", excludedRootDirs);
        if (roots == null) {
            return;
        }
        List<File> rootDirs = Arrays.asList(roots);
        for (File rootDir : rootDirs) {
            if (!excludedRootDirs.contains(rootDir) && FileUtil.isReadableDirectory(rootDir)) {
                DefaultMutableTreeNode rootDirNode = new SortedChildrenTreeNode(rootDir);
                insertNodeInto(rootDirNode, rootNode, rootNode.getChildCount());
                addDummy(rootDirNode);
            }
        }
    }

    private void addChildren(DefaultMutableTreeNode parentNode) {
        Object userObject = parentNode.getUserObject();
        File parentDir = userObject instanceof File ? (File) userObject : null;
        if (!FileUtil.isReadableDirectory(parentDir)) {
            return;
        }
        LOGGER.log(Level.FINEST, "Reading subdirectories of ''{0}''...", parentDir);
        File[] subdirs = parentDir.listFiles(directoryFilter);
        LOGGER.log(Level.FINEST, "Subdirectories of ''{0}'' has been read: {1}", new Object[]{parentDir, StringUtil.toString(subdirs)});
        removeDummy(parentNode, false);
        if (subdirs == null) {
            return;
        }
        List<File> parentNodeChildDirectories = getChildDirectories(parentNode);
        for (File subdir : subdirs) {
            if (!parentNodeChildDirectories.contains(subdir) && FileUtil.isReadableDirectory(subdir)) {
                DefaultMutableTreeNode childNode = addChildDirectory(parentNode, subdir);
                addDummy(childNode);
            }
        }
    }

    private static List<File> getChildDirectories(DefaultMutableTreeNode node) {
        if (node == null) {
            return Collections.emptyList();
        }
        int childCount = node.getChildCount();
        List<File> childFiles = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            Object childNodeUserObject = childNode.getUserObject();
            if (childNodeUserObject instanceof File) {
                childFiles.add((File) childNodeUserObject);
            }
        }
        return childFiles;
    }

    private DefaultMutableTreeNode addChildDirectory(DefaultMutableTreeNode parentNode, File directory) {
        LOGGER.log(Level.FINEST, "Adding subdirectory ''{0}'' to node ''{1}''", new Object[]{directory, parentNode});
        DefaultMutableTreeNode newChildNode = new SortedChildrenTreeNode(directory);
        parentNode.add(newChildNode);
        int newChildIndex = parentNode.getIndex(newChildNode);
        fireTreeNodesInserted(this, parentNode.getPath(), new int[]{newChildIndex}, new Object[]{newChildNode});
        LOGGER.log(Level.FINEST, "Subdirectory ''{0}'' has been added to node ''{1}''", new Object[]{directory, parentNode});
        return newChildNode;
    }

    private void addDummy(DefaultMutableTreeNode parentNode) {
        Object userObject = parentNode.getUserObject();
        if (parentNode.isLeaf()
                && userObject instanceof File
                && FileUtil.containsReadableDirectory((File) userObject)) {
             // Must be created dynamically, because parent will be set and must be different (no static tree node)
            SortedChildrenTreeNode dummy = new SortedChildrenTreeNode(DUMMY);
            parentNode.add(dummy);
            fireTreeNodesInserted(this, parentNode.getPath(), new int[]{0}, new Object[]{dummy});
        }
    }

    private void removeDummy(DefaultMutableTreeNode parentNode, boolean onlyIfEmptyDir) {
        if (containsDummy(parentNode)) {
            TreeNode dummyChild = parentNode.getChildAt(0);
            boolean remove = true;
            if (onlyIfEmptyDir) {
                Object userObject = parentNode.getUserObject();
            if (userObject instanceof File) {
                    remove = !FileUtil.containsReadableDirectory((File) userObject);
            }
            }
            if (remove) {
                parentNode.remove(0);
                fireTreeNodesRemoved(this, parentNode.getPath(), new int[]{0}, new Object[]{dummyChild});
        }
        }
    }

    private boolean containsDummy(DefaultMutableTreeNode node) { // Quick & Dirty, but faster; relies on proper insertions/removes
        return node.getChildCount() == 1
                && ((DefaultMutableTreeNode)node.getChildAt(0)).getUserObject() == DUMMY;
    }

    /**
     * Creates a new directory as child of a node. Let's the user input the new name and inserts the new created
     * directory.
     *
     * @param parentNode parent node. If null, nothing will be done.
     * @return created directory or null if not created
     */
    public File createDirectoryIn(DefaultMutableTreeNode parentNode) {
        File parentNodeDirectory = parentNode == null
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
        tree.expandPath(new TreePath(node.getPath()));
        while (node != null && !filePath.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(node, filePath.pop());
            if (node != null && filePath.size() > 0) {
                tree.expandPath(new TreePath(node.getPath()));
            }
        }
        if (node != null) {
            TreePath nodePath = new TreePath(node.getPath());
            tree.scrollPathToVisible(nodePath);
                if (select) {
                tree.setSelectionPath(nodePath);
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
            TreeNode parent = node.getParent();
            removeChildrenWithNotExistingFiles((DefaultMutableTreeNode) parent);
            addChildren((DefaultMutableTreeNode) parent);
            for (Enumeration<?> e = parent.children(); e.hasMoreElements();) {
                Object sibling = e.nextElement();
                addDummy((DefaultMutableTreeNode) sibling);
                }
            }
        tree.setCursor(treeCursor);
    }

    private int removeChildrenWithNotExistingFiles(DefaultMutableTreeNode parentNode) {
        int childCount = parentNode.getChildCount();
        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            if (containsDummy(child)) {
                removeDummy(child, true);
            }
            Object userObject = child.getUserObject();
            File file = null;
            if (userObject instanceof File) {
                file = (File) userObject;
            }
            if (file != null && !file.exists()) {
                nodesToRemove.add(child);
            }
        }
        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            LOGGER.log(Level.FINEST, "Removing child node ''{0}'' from parent node ''{1}''...", new Object[]{childNodeToRemove, parentNode});
            removeNodeFromParent(childNodeToRemove);
            LOGGER.log(Level.FINEST, "Child node ''{0}'' has been removed from parent node ''{1}''", new Object[]{childNodeToRemove, parentNode});
        }
        return nodesToRemove.size();
    }

    private List<DefaultMutableTreeNode> getTreeRowNodes() {
        int rowCount = tree.getRowCount();
        List<DefaultMutableTreeNode> nodes = new ArrayList<>(rowCount);
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
            addChildren(node);
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

        private final AtomicInteger taskCount = new AtomicInteger(0);

        @Override
        public void run() {
            if (taskCount.intValue() > 0) {
                return;
            }
            addNewRoots();
            update(getTreeRowNodes());
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
                        boolean isExclude = excludedRootDirs.contains(rootFile);
                        if (!isExclude && rootFile.isDirectory() && !childDirectories.contains(rootFile)) {
                            DefaultMutableTreeNode child = addChildDirectory(rootNode, rootFile);
                            addDummy(child);
                        }
                    }
                    taskCount.decrementAndGet();
                }
            });
        }

        private void update(Collection<? extends DefaultMutableTreeNode> nodes) {
            for (DefaultMutableTreeNode node : nodes) {
                updateRemoveDummy(node);
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

        private void updateRemoveDummy(final DefaultMutableTreeNode node) {
            taskCount.incrementAndGet();
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    removeDummy(node, true);
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
            Object userObject = node.getUserObject();
            if (!(userObject instanceof File)) { // e.g. through the root or a dummy node
                taskCount.decrementAndGet();
                return;
            }
            if (!TreeUtil.isNodeExpanded(tree, node)) {
                addDummy(node);
                taskCount.decrementAndGet();
                return;
            }
            final List<File> childDirectories = getChildDirectories(node);
            final List<File> directories = FileUtil.listFiles((File) userObject, directoryFilter);
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    for (File directory : directories) {
                        if (!childDirectories.contains(directory) && FileUtil.isReadableDirectory(directory)) {
                            DefaultMutableTreeNode child = addChildDirectory(node, directory);
                            addDummy(child);
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

    // JDK WatchService does not work under Windows 7 and UAC. Code without TreeModelListener for Updates:
//    private class TreeNodeUpdater implements Runnable {
//
//        private final WatchService watchService = createWatchService();
//        private volatile boolean stop;
//
//        private WatchService createWatchService() {
//            try {
//                return FileSystems.getDefault().newWatchService();
//            } catch (IOException ex) {
//                Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName()).log(Level.SEVERE, null, ex);
//                return null;
//            }
//        }
//
//        private void init() {
//            SwingUtilities.invokeLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    registerAllModelPathsWithWatchService();
//                }
//            });
//        }
//
//        private void registerAllModelPathsWithWatchService() {
//            for (DefaultMutableTreeNode node : getTreeRowNodes()) {
//                Object userObject = node.getUserObject();
//                if (userObject instanceof File) {
//                    registerPathWithWatchService(((File) userObject).toPath());
//                }
//            }
//        }
//
//        private void registerPathWithWatchService(Path path) {
//            try {
//                path.register(watchService,
//                        StandardWatchEventKinds.ENTRY_CREATE,
//                        StandardWatchEventKinds.ENTRY_DELETE);
//            } catch (Throwable t) {
//                Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName()).log(Level.SEVERE, null, t);
//            }
//        }
//
//        @Override
//        public void run() {
//            if (watchService == null) {
//                Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName()).log(Level.WARNING, "Can't auto update dircetorie's view: WatchService is null");
//                return;
//            }
//            try {
//                init();
//                while (!stop) {
//                    try {
//                        WatchKey watchKey = watchService.take();
//                        for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
//                            Kind<?> kind = watchEvent.kind();
//                            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
//                                WatchEvent<Path> watchEventPath = (WatchEvent<Path>)watchEvent;
//                                Path path = watchEventPath.context();
//                                addNode(path.toFile());
//                            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
//                                WatchEvent<Path> watchEventPath = (WatchEvent<Path>)watchEvent;
//                                Path path = watchEventPath.context();
//                                removeNode(path.toFile());
//                            }
//
//                        }
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            } finally {
//                closeWatchService();
//            }
//        }
//
//        private void addNode(final File file) {
//            SwingUtilities.invokeLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    File parentDir = file.getParentFile();
//                    if (parentDir != null) {
//                        DefaultMutableTreeNode parentNode = TreeUtil.findNodeContainingUserObjectRecursive(rootNode, parentDir);
//                        if (parentNode == null) {
//                            Logger.getLogger(TreeNodeUpdater.class.getName()).log(Level.WARNING, "Parent directory node not found: {0}", parentDir);
//                            return;
//                        }
//                        boolean dirNodeAlreadyExists = TreeUtil.findNodeWithUserObject(parentNode, parentDir) != null;
//                        if (!dirNodeAlreadyExists) {
//                            addChildDirectory(parentNode, file);
//                            registerPathWithWatchService(file.toPath());
//                        }
//                    }
//                }
//            });
//        }
//
//        private void removeNode(final File file) {
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    DefaultMutableTreeNode node = TreeUtil.findNodeContainingUserObjectRecursive(rootNode, file);
//                    if (node == null) {
//                        Logger.getLogger(TreeNodeUpdater.class.getName()).log(Level.WARNING, "Directory node not found: {0}", file);
//                        return;
//                    }
//                    removeNodeFromParent(node);
//                }
//            });
//        }
//
//        private void closeWatchService() {
//            if (watchService != null) {
//                try {
//                    watchService.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(AllSystemDirectoriesTreeModel.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//        private void stop() {
//            stop = true;
//        }
//    }

}
