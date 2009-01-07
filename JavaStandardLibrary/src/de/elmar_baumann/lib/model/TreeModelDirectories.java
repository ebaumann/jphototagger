package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.types.SortType;
import de.elmar_baumann.lib.util.FileComparator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Model für Verzeichnisse ohne Dateien (Verzeichnisauswahl).
 * Stellt alle Wurzelverzeichnisse des Systems dar.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TreeModelDirectories implements TreeModel {

    private static final int updateIntervalSeconds = 3;
    private static final FileComparator sortComparator = new FileComparator(SortType.ascendingNoCase);
    private final List<File> rootNodes = new ArrayList<File>();
    private final Map<File, List<File>> childrenOfNode = new HashMap<File, List<File>>();
    private final List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private final List<File> filesForUpdateCheck = new LinkedList<File>();
    private Object root = new Object();
    private boolean acceptHidden;
    private DirectoryFilter directoryFilter;
    private ScanForDirectoryUpdates updater;

    public TreeModelDirectories(boolean accecptHidden) {
        this.acceptHidden = accecptHidden;
        directoryFilter = new DirectoryFilter(acceptHidden);
        setRootDirectories();
        startUpdater();
    }

    private void setRootDirectories() {
        File[] roots = File.listRoots();
        TreePath parentPath = new TreePath(new Object[]{root});

        for (File dir : roots) {
            insertNode(parentPath, dir);
        }
    }

    private void startUpdater() {
        if (updater != null) {
            updater.setStop(true);
        }
        updater = new ScanForDirectoryUpdates();
        updater.start();
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(root)) {
            synchronized (rootNodes) {
                return rootNodes.size();
            }
        }
        List<File> children;
        synchronized (childrenOfNode) {
            children = childrenOfNode.get(parent);
        }
        if (children == null) {
            new Thread(new AddSubdirectories((File) parent)).run();
            return 0;
        } else {
            return children.size();
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        File file;
        if (parent.equals(root)) {
            synchronized (rootNodes) {
                file = rootNodes.get(index);
                addToUpdateChecks(file);
                return file;
            }
        }
        synchronized (childrenOfNode) {
            file = childrenOfNode.get(parent).get(index);
        }
        addToUpdateChecks(file);
        return file;
    }

    private void addToUpdateChecks(File f) {
        synchronized (filesForUpdateCheck) {
            if (f != null && !filesForUpdateCheck.contains(f)) {
                filesForUpdateCheck.add(f);
            }
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) <= 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(root)) {
            synchronized (rootNodes) {
                return rootNodes.indexOf(child);
            }
        }
        synchronized (childrenOfNode) {
            return childrenOfNode.get(parent).indexOf(child);
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void notifyNodesInserted(TreeModelEvent evt) {
        synchronized (listeners) {
            for (TreeModelListener l : listeners) {
                l.treeNodesInserted(evt);
            }
        }
    }

    private void notifyNodesRemoved(TreeModelEvent evt) {
        synchronized (listeners) {
            for (TreeModelListener l : listeners) {
                l.treeNodesRemoved(evt);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<File> getSubDirectories(File file) {
        List<File> subDirectories = new ArrayList<File>();
        File[] listFiles = file.listFiles(directoryFilter);

        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                subDirectories.add(listFiles[i]);
            }
            Collections.sort(subDirectories, sortComparator);
        }
        return subDirectories;
    }

    @SuppressWarnings("unchecked")
    private void insertNode(TreePath parentPath, File node) {
        Object parent = parentPath.getLastPathComponent();
        if (parent == root) {
            insertRootNode(node);
        } else {
            insertChildNode(parentPath, node);
        }
    }

    @SuppressWarnings("unchecked")
    private void insertRootNode(File node) {
        int index;
        synchronized (rootNodes) {
            rootNodes.add(node);
            Collections.sort(rootNodes, sortComparator);
            index = rootNodes.indexOf(node);
        }
        synchronized (childrenOfNode) {
            childrenOfNode.put(node, null);
        }
        TreeModelEvent evt = null;
        synchronized (rootNodes) {
            evt = new TreeModelEvent(this, new Object[]{root}, new int[]{index}, new Object[]{node});
        }
        notifyNodesInserted(evt);
    }

    @SuppressWarnings("unchecked")
    private void insertChildNode(TreePath parentPath, File node) {
        Object parent = parentPath.getLastPathComponent();
        List<File> parentsChildren;
        synchronized (childrenOfNode) {
            parentsChildren = childrenOfNode.get(parent);
        }
        if (parentsChildren == null) {
            List<File> newChildren = new ArrayList<File>();
            newChildren.add(node);
            synchronized (childrenOfNode) {
                childrenOfNode.put((File) parent, newChildren);
                childrenOfNode.put(node, null);
            }
            TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(), new int[]{0}, new Object[]{node});
            notifyNodesInserted(evt);
        } else {
            boolean contains = false;
            synchronized (parentsChildren) {
                contains = parentsChildren.contains(node);
            }
            if (!contains) {
                int index;
                synchronized (parentsChildren) {
                    parentsChildren.add(node);
                    Collections.sort(parentsChildren, sortComparator);
                    index = parentsChildren.indexOf(node);
                }
                synchronized (childrenOfNode) {
                    childrenOfNode.put(node, null);
                }
                TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(), new int[]{index}, new Object[]{node});
                notifyNodesInserted(evt);
            }
        }
    }

    private void removeNode(TreePath parentPath, File node) {
        updater.setPause(true);
        synchronized (childrenOfNode) {
            removeChildrenOf(node);
            File parentFile = (File) parentPath.getLastPathComponent();
            List<File> parentsFiles = childrenOfNode.get(parentFile);
            if (parentsFiles != null) {
                int indexOfNode = parentsFiles.indexOf(node);
                parentsFiles.remove(node);
                TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(),
                    new int[]{indexOfNode}, new Object[]{node});
                notifyNodesRemoved(evt);
            }
            childrenOfNode.remove(node);
            synchronized (rootNodes) {
                rootNodes.remove(node);
            }
            synchronized (filesForUpdateCheck) {
                filesForUpdateCheck.remove(node);
            }
        }
        updater.setPause(false);
    }

    private void removeChildrenOf(File node) {
        synchronized (filesForUpdateCheck) {
            List<File> cachedFiles = getCachedFiles();
            String nodeName = node.getAbsolutePath() + File.separator;
            synchronized (childrenOfNode) {
                for (File cachedFile : cachedFiles) {
                    if (cachedFile.getAbsolutePath().startsWith(nodeName)) {
                        childrenOfNode.remove(cachedFile);
                        filesForUpdateCheck.remove(cachedFile);
                    }
                }
            }
        }
    }

    private List<File> getCachedFiles() {
        List<File> cachedFiles = new ArrayList<File>();
        synchronized (childrenOfNode) {
            Set<File> files = childrenOfNode.keySet();
            for (File file : files) {
                cachedFiles.add(file);
            }
        }
        return cachedFiles;
    }

    private TreePath getTreePath(File file) {
        Stack<File> stack = new Stack<File>();
        stack.push(file);
        File parentFile = file.getParentFile();
        while (parentFile != null) {
            stack.push(parentFile);
            parentFile =
                parentFile.getParentFile();
        }

        Object[] path = new Object[stack.size() + 1];
        path[0] = root;
        int i = 1;
        while (!stack.isEmpty()) {
            path[i++] = stack.pop();
        }

        return new TreePath(path);
    }

    private class AddSubdirectories
        implements Runnable {

        File parent;

        AddSubdirectories(File parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            List<File> subdirectories =
                getSubDirectories(parent);
            TreePath parentPath = getTreePath(parent);
            for (File dir : subdirectories) {
                insertNode(parentPath, dir);
            }
        }
    }

    private class ScanForDirectoryUpdates extends Thread {

        private boolean stop = false;
        private boolean pause = false;

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        public void setPause(boolean pause) {
            this.pause = pause;
        }

        @Override
        public void run() {
            while (!stop) {
                if (!pause) {
                    try {
                        Thread.sleep(updateIntervalSeconds * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ScanForDirectoryUpdates.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    checkInserted();
                    checkRemoved();
                }
            }
        }

        private void checkInserted() {
            List<File> files = getFilesForUdateCheck();
            for (File file : files) {
                TreePath parentPath = getTreePath(file);
                File[] childrenOfFile = file.listFiles(directoryFilter);
                if (childrenOfFile != null) {
                    for (File childOfFile : childrenOfFile) {
                        boolean childExists = false;
                        List<File> existingChildrenOfFile;
                        synchronized (childrenOfNode) {
                            existingChildrenOfFile = childrenOfNode.get(file);
                            childExists = existingChildrenOfFile != null &&
                                existingChildrenOfFile.contains(childOfFile);
                        }
                        if (!childExists) {
                            insertNode(parentPath, childOfFile);
                        }
                    }
                }
            }
        }

        private void checkRemoved() {
            List<File> files = getFilesForUdateCheck();
            for (File file : files) {
                if (!isRootNode(file) && !file.exists()) {
                    TreePath path = getTreePath(file).getParentPath();
                    removeNode(path, file);
                }
            }
        }

        private boolean isRootNode(File file) {
            synchronized (rootNodes) {
                return rootNodes.contains(file);
            }
        }

        private List<File> getFilesForUdateCheck() {
            List<File> files = new ArrayList<File>();
            synchronized (filesForUpdateCheck) {
                for (File file : filesForUpdateCheck) {
                    files.add(file);
                }
            }
            return files;
        }
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        updater.setStop(true);
    }
}
