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
import javax.swing.SwingUtilities;
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
public final class TreeModelAllSystemDirectories implements TreeModel {

    private static final int UPDATE_INTERVAL_SECONDS = 3;
    private static final ComparatorFilesNames SORT_COMPARATOR =
            ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE;
    private final List<File> rootNodes = Collections.synchronizedList(
            new ArrayList<File>());
    private final Map<File, List<File>> childrenOfNode = Collections.
            synchronizedMap(new HashMap<File, List<File>>());
    private final List<TreeModelListener> listeners = Collections.
            synchronizedList(new ArrayList<TreeModelListener>());
    private final List<File> filesForUpdateCheck = Collections.synchronizedList(
            new LinkedList<File>());
    private final Object root = new Object();
    private final DirectoryFilter directoryFilter;
    private ScanForDirectoryUpdates updater;
    private final Object monitor = new Object();

    public TreeModelAllSystemDirectories(Set<DirectoryFilter.Option> options) {
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
            List<File> children = childrenOfNode.get(parent);
            if (children == null) {
                Thread thread = new Thread(new AddSubdirectories((File) parent));
                thread.setName("Adding subdirectories of " + parent + " @ " + // NOI18N
                        getClass().getName()); // NOI18N
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
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
        synchronized (filesForUpdateCheck) {
            if (f != null && !filesForUpdateCheck.contains(f)) {
                filesForUpdateCheck.add(f);
            }
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
            return childrenOfNode.get(parent) == null
                   ? 0
                   : childrenOfNode.get(parent).indexOf(child);
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

    private void notifyNodesInserted(final TreeModelEvent evt) {
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(evt);
        }
    }

    private void notifyNodesRemoved(final TreeModelEvent evt) {
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
            Collections.sort(subDirectories, SORT_COMPARATOR);
        }
        return subDirectories;
    }

    @SuppressWarnings("unchecked")
    private void insertNode(final TreePath parentPath, final File node) {
        Object parent = parentPath.getLastPathComponent();
        if (parent == root) {
            insertRootNode(node);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    insertChildNode(parentPath, node);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void insertRootNode(final File file) {
        int index;
        synchronized (monitor) {
            rootNodes.add(file);
            Collections.sort(rootNodes, SORT_COMPARATOR);
            index = rootNodes.indexOf(file);
            childrenOfNode.put(file, null);
        }
        TreeModelEvent evt = null;
        evt = new TreeModelEvent(this, new Object[]{root}, new int[]{
                    index},
                new Object[]{file});
        notifyNodesInserted(evt);
    }

    @SuppressWarnings("unchecked")
    private void insertChildNode(TreePath parentPath, File file) {
        Object parent = parentPath.getLastPathComponent();
        List<File> parentsChildren = childrenOfNode.get(parent);
        if (parentsChildren == null) {
            List<File> newChildren = new ArrayList<File>();
            newChildren.add(file);
            synchronized (monitor) {
                childrenOfNode.put((File) parent, newChildren);
                childrenOfNode.put(file, null);
            }
            TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(),
                    new int[]{0}, new Object[]{file});
            notifyNodesInserted(evt);
        } else {
            boolean contains = false;
            contains = parentsChildren.contains(file);
            if (!contains) {
                int index;
                synchronized (monitor) {
                    parentsChildren.add(file);
                    Collections.sort(parentsChildren, SORT_COMPARATOR);
                    index = parentsChildren.indexOf(file);
                    childrenOfNode.put(file, null);
                }
                TreeModelEvent evt = new TreeModelEvent(this,
                        parentPath.getPath(), new int[]{index}, new Object[]{
                            file});
                notifyNodesInserted(evt);
            }
        }
    }

    private void removeNode(final TreePath parentPath, final File node) {
        updater.setPause(true);
        removeChildrenOf(node);
        File parentFile = (File) parentPath.getLastPathComponent();
        List<File> parentsFiles = new ArrayList<File>(childrenOfNode.get(
                parentFile));
        if (parentsFiles != null) {
            int indexOfNode = parentsFiles.indexOf(node);
            parentsFiles.remove(node);
            TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(),
                    new int[]{indexOfNode}, new Object[]{node});
            notifyNodesRemoved(evt);
        }
        childrenOfNode.remove(node);
        rootNodes.remove(node);
        synchronized (filesForUpdateCheck) {
            filesForUpdateCheck.remove(node);
        }
        updater.setPause(false);
    }

    private void removeChildrenOf(File node) {
        List<File> cachedFiles;
        synchronized (monitor) {
            cachedFiles = new ArrayList<File>(childrenOfNode.keySet());
        }
        String nodeName = node.getAbsolutePath() + File.separator;
        for (File cachedFile : cachedFiles) {
            if (cachedFile.getAbsolutePath().startsWith(nodeName)) {
                childrenOfNode.remove(cachedFile);
                synchronized (filesForUpdateCheck) {
                    filesForUpdateCheck.remove(cachedFile);
                }
            }
        }
    }

    private TreePath getTreePath(File file) {
        Stack<File> stack = new Stack<File>();
        stack.push(file);
        File parentFile = file.getParentFile();
        while (parentFile != null) {
            stack.push(parentFile);
            parentFile = parentFile.getParentFile();
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

        public ScanForDirectoryUpdates() {
            setPriority(MIN_PRIORITY);
            setName("Scanning directories for updates" + " @ " + // NOI18N
                    getClass().getName());
        }

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
                        Thread.sleep(UPDATE_INTERVAL_SECONDS * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ScanForDirectoryUpdates.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                    checkInserted();
                    checkRemoved();
                }
            }
        }

        private void checkInserted() {
            List<File> files;
            synchronized (filesForUpdateCheck) {
                files = new ArrayList<File>(filesForUpdateCheck);
            }
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
            List<File> files;
            synchronized (filesForUpdateCheck) {
                files = new ArrayList<File>(filesForUpdateCheck);
            }
            for (File file : files) {
                if (!isRootNode(file) && !file.exists()) {
                    TreePath path = getTreePath(file).getParentPath();
                    removeNode(path, file);
                }
            }
        }

        private boolean isRootNode(File file) {
            return rootNodes.contains(file);
        }
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        updater.setStop(true);
    }
}
