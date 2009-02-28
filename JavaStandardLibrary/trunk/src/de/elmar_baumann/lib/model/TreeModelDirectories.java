package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.comparator.ComparatorFilesNames;
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
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TreeModelDirectories implements TreeModel {

    private static final int updateIntervalSeconds = 3;
    private static final ComparatorFilesNames sortComparator = ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE;
    private final List<File> rootNodes = Collections.synchronizedList(new ArrayList<File>());
    private final Map<File, List<File>> childrenOfNode = Collections.synchronizedMap(new HashMap<File, List<File>>());
    private final List<TreeModelListener> listeners = Collections.synchronizedList(new ArrayList<TreeModelListener>());
    private final List<File> filesForUpdateCheck = Collections.synchronizedList(new LinkedList<File>());
    private final Object root = new Object();
    private final DirectoryFilter directoryFilter;
    private ScanForDirectoryUpdates updater;
    private final Object monitor = new Object();

    public TreeModelDirectories(Set<DirectoryFilter.Option> options) {
        if (options == null) {
            throw new NullPointerException("options == null");
        }

        directoryFilter = new DirectoryFilter(options);
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
        synchronized (monitor) {
            if (parent.equals(root)) {
                return rootNodes.size();
            }
            List<File> children;
            children = childrenOfNode.get(parent);
            if (children == null) {
                new Thread(new AddSubdirectories((File) parent)).run();
                return 0;
            } else {
                return children.size();
            }
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        synchronized (monitor) {
            File file;
            if (parent.equals(root)) {
                file = rootNodes.get(index);
                addToUpdateChecks(file);
                return file;
            }
            file = childrenOfNode.get(parent).get(index);
            addToUpdateChecks(file);
            return file;
        }
    }

    private void addToUpdateChecks(File f) {
        if (f != null && !filesForUpdateCheck.contains(f)) {
            filesForUpdateCheck.add(f);
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        synchronized (monitor) {
            return getChildCount(node) <= 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        synchronized (monitor) {
            if (parent.equals(root)) {
                return rootNodes.indexOf(child);
            }
            return childrenOfNode.get(parent).indexOf(child);
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        synchronized (monitor) {
            listeners.add(l);
        }
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        synchronized (monitor) {
            listeners.remove(l);
        }
    }

    private void notifyNodesInserted(TreeModelEvent evt) {
        assert evt != null : evt;

        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(evt);
        }
    }

    private void notifyNodesRemoved(TreeModelEvent evt) {
        assert evt != null : evt;

        for (TreeModelListener l : listeners) {
            l.treeNodesRemoved(evt);
        }
    }

    @SuppressWarnings("unchecked")
    private List<File> getSubDirectories(File file) {
        assert file != null : file;

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
        assert parentPath != null : parentPath;
        assert node != null : node;

        Object parent = parentPath.getLastPathComponent();
        if (parent == root) {
            insertRootNode(node);
        } else {
            insertChildNode(parentPath, node);
        }
    }

    @SuppressWarnings("unchecked")
    private void insertRootNode(File node) {
        assert node != null : node;

        int index;
        rootNodes.add(node);
        Collections.sort(rootNodes, sortComparator);
        index = rootNodes.indexOf(node);
        childrenOfNode.put(node, null);
        TreeModelEvent evt = null;
        evt = new TreeModelEvent(this, new Object[]{root}, new int[]{index}, new Object[]{node});
        notifyNodesInserted(evt);
    }

    @SuppressWarnings("unchecked")
    private void insertChildNode(TreePath parentPath, File node) {
        assert parentPath != null : parentPath;
        assert node != null : node;

        Object parent = parentPath.getLastPathComponent();
        List<File> parentsChildren;
        parentsChildren = childrenOfNode.get(parent);
        if (parentsChildren == null) {
            List<File> newChildren = new ArrayList<File>();
            newChildren.add(node);
            childrenOfNode.put((File) parent, newChildren);
            childrenOfNode.put(node, null);
            TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(), new int[]{0}, new Object[]{node});
            notifyNodesInserted(evt);
        } else {
            boolean contains = false;
            contains = parentsChildren.contains(node);
            if (!contains) {
                int index;
                parentsChildren.add(node);
                Collections.sort(parentsChildren, sortComparator);
                index = parentsChildren.indexOf(node);
                childrenOfNode.put(node, null);
                TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(), new int[]{index}, new Object[]{node});
                notifyNodesInserted(evt);
            }
        }
    }

    private void removeNode(TreePath parentPath, File node) {
        assert parentPath != null : parentPath;
        assert node != null : node;

        updater.setPause(true);
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
        rootNodes.remove(node);
        filesForUpdateCheck.remove(node);
        updater.setPause(false);
    }

    private void removeChildrenOf(File node) {
        assert node != null : node;

        List<File> cachedFiles = getCachedFiles();
        String nodeName = node.getAbsolutePath() + File.separator;
        for (File cachedFile : cachedFiles) {
            if (cachedFile.getAbsolutePath().startsWith(nodeName)) {
                childrenOfNode.remove(cachedFile);
                filesForUpdateCheck.remove(cachedFile);
            }
        }
    }

    private List<File> getCachedFiles() {
        List<File> cachedFiles = new ArrayList<File>();
        Set<File> files = childrenOfNode.keySet();
        for (File file : files) {
            cachedFiles.add(file);
        }
        return cachedFiles;
    }

    private TreePath getTreePath(File file) {
        assert file != null : file;

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
            assert parent != null : parent;
            this.parent = parent;
        }

        @Override
        public void run() {
            List<File> subdirectories = getSubDirectories(parent);
            TreePath parentPath = getTreePath(parent);
            for (File dir : subdirectories) {
                insertNode(parentPath, dir);
            }
        }
    }

    private class ScanForDirectoryUpdates extends Thread {

        private boolean stop = false;
        private boolean pause = false;

        public synchronized void setStop(boolean stop) {
            this.stop = stop;
        }

        public synchronized void setPause(boolean pause) {
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
                        synchronized (monitor) {
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
            assert file != null : file;

            return rootNodes.contains(file);
        }

        private List<File> getFilesForUdateCheck() {
            List<File> files = new ArrayList<File>();
            for (File file : filesForUpdateCheck) {
                files.add(file);
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
