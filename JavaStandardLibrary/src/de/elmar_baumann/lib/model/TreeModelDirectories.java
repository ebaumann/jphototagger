package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.types.SortType;
import de.elmar_baumann.lib.util.FileComparator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
public class TreeModelDirectories implements TreeModel {

    private Object root = new Object();
    private List<File> rootDirectories = new ArrayList<File>();
    private Map<File, List<File>> childrenOfNode = new HashMap<File, List<File>>();
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private boolean accecptHidden;

    public TreeModelDirectories(boolean accecptHidden) {
        this.accecptHidden = accecptHidden;
        setRootDirectories();
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(root)) {
            synchronized (rootDirectories) {
                return rootDirectories.size();
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
        if (parent.equals(root)) {
            synchronized (rootDirectories) {
                return rootDirectories.get(index);
            }
        }
        synchronized (childrenOfNode) {
            return childrenOfNode.get(parent).get(index);
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) <= 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(root)) {
            synchronized (rootDirectories) {
                return rootDirectories.indexOf(child);
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
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    private void notifyAdded(TreeModelEvent evt) {
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(evt);
        }
    }

    @SuppressWarnings("unchecked")
    private List<File> getSubDirectories(File file, SortType sortType, boolean acceptHidden) {
        List<File> subDirectories = new ArrayList<File>();
        File[] listFiles = file.listFiles(new DirectoryFilter(acceptHidden));

        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                subDirectories.add(listFiles[i]);
            }
            Collections.sort(subDirectories, new FileComparator(sortType));
        }
        return subDirectories;
    }

    private void add(TreePath parentPath, File newFile) {
        Object parent = parentPath.getLastPathComponent();
        if (parent == root) {
            synchronized (rootDirectories) {
                rootDirectories.add(newFile);
            }
            TreeModelEvent evt = null;
            synchronized (childrenOfNode) {
                childrenOfNode.put(newFile, null);
            }
            synchronized (rootDirectories) {
                evt = new TreeModelEvent(this, new Object[]{root},
                        new int[]{rootDirectories.size() - 1}, new Object[]{newFile});
            }
            notifyAdded(evt);
        } else {
            List<File> children;
            synchronized (childrenOfNode) {
                children = childrenOfNode.get(parent);
            }
            if (children == null) {
                List<File> newChildren = new ArrayList<File>();
                newChildren.add(newFile);
                synchronized (childrenOfNode) {
                    childrenOfNode.put((File) parent, newChildren);
                    childrenOfNode.put(newFile, null);
                }
                TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(),
                        new int[]{0}, new Object[]{newFile});
                notifyAdded(evt);
            } else {
                if (!children.contains(newFile)) {
                    children.add(newFile);
                    TreeModelEvent evt = new TreeModelEvent(this, parentPath.getPath(),
                            new int[]{children.size() - 1}, new Object[]{newFile});
                    notifyAdded(evt);
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

    private class AddSubdirectories implements Runnable {

        File parent;

        AddSubdirectories(File parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            List<File> subdirectories =
                    getSubDirectories(parent, SortType.ascendingNoCase, accecptHidden);
            TreePath parentPath = getTreePath(parent);
            for (File dir : subdirectories) {
                add(parentPath, dir);
            }
        }
    }

    private void setRootDirectories() {
        File[] roots = File.listRoots();
        TreePath parentPath = new TreePath(new Object[]{root});

        for (File dir : roots) {
            add(parentPath, dir);
        }
    }
}
