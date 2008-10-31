package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.io.DirectoryTreeModelRoots;
import de.elmar_baumann.lib.types.SortType;
import de.elmar_baumann.lib.util.FileComparator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class TreeModelDirectories implements TreeModel {

    private DirectoryTreeModelRoots root = new DirectoryTreeModelRoots();
    private Map<File, List<File>> childrenOfParent = new HashMap<File, List<File>>();
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private boolean accecptHidden;

    public TreeModelDirectories(boolean accecptHidden) {
        this.accecptHidden = accecptHidden;
        init();
    }

    private void init() {
        int count = root.getChildCount();
        for (int i = 0; i < count; i++) {
            File child = root.getChild(i);
            childrenOfParent.put(child,
                getSubDirectories(child, SortType.ascendingNoCase, accecptHidden));
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent.equals(root)) {
            return root.getChild(index);
        }
        File child = childrenOfParent.get(parent).get(index);
        if (!childrenOfParent.containsKey(child)) {
            childrenOfParent.put(child,
                getSubDirectories(child, SortType.ascendingNoCase, accecptHidden));
        }
        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(root)) {
            return root.getChildCount();
        }
        List<File> children = childrenOfParent.get(parent);
        if (children != null) {
            return children.size();
        }
        File p = (File) parent;
        List<File> subdirectories =
            getSubDirectories(p, SortType.ascendingNoCase, accecptHidden);
        childrenOfParent.put(p, subdirectories);
        return subdirectories.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node.equals(root)) {
            return root.getChildCount() <= 0;
        }
        return childrenOfParent.get(node) == null ||
            childrenOfParent.get(node).size() <= 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(root)) {
            return root.getIndexOfChild(child);
        }
        return childrenOfParent.get(parent).indexOf(child);
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

    private void notifyChanged(TreePath path) {
        TreeModelEvent evt = new TreeModelEvent(this, path);
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(evt);
        }
    }

    // Logging to detect most time consuming directories
    @SuppressWarnings("unchecked")
    private List<File> getSubDirectories(File file, SortType sortType, boolean acceptHidden) {
        
        long startMillis = System.currentTimeMillis();
        File[] listFiles = file.listFiles(new DirectoryFilter(acceptHidden));
        long duration = System.currentTimeMillis() - startMillis;
        Logger.getLogger(TreeModelDirectories.class.getName()).log(Level.FINEST, "Duration of listing '" + file + "': " + duration + " Milliseconds");
        
        List<File> directories = new ArrayList<File>();

        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                directories.add(listFiles[i]);
            }
            Collections.sort(directories, new FileComparator(sortType));
        }
        return directories;
    }

    /**
     * Removes a file.
     * <em>Does not change the file system, only the model!</em>
     * 
     * @param  path  file to remove
     * @return true if removed
     */
    public boolean remove(TreePath path) {
        Object file = path.getLastPathComponent();
        TreePath parentPath = path.getParentPath();
        Object parent = parentPath == null ? null : parentPath.getLastPathComponent();
        if (parent == root && root.remove((File) file)) {
            notifyChanged(parentPath);
            return true;
        } else if (parent != null) {
            List<File> children = childrenOfParent.get(parent);
            if (children != null && children.remove(file)) {
                notifyChanged(parentPath);
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces a file.
     * <em>Does not change the file system, only the model!</em>
     * 
     * @param path     old file
     * @param newFile  new file
     * @return         true if replaced
     */
    public boolean replace(TreePath path, File newFile) {
        Object oldValue = path.getLastPathComponent();
        TreePath parentPath = path.getParentPath();
        Object parent = parentPath == null ? null : parentPath.getLastPathComponent();
        if (parent == root && root.replace((File) oldValue, newFile)) {
            notifyChanged(parentPath);
            return true;
        } else if (parent != null) {
            List<File> children = childrenOfParent.get(parent);
            if (children != null) {
                int index = children.indexOf(oldValue);
                if (index >= 0) {
                    children.set(index, newFile);
                    notifyChanged(parentPath);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds a file.
     * <em>Does not change the file system, only the model!</em>
     * 
     * @param  parentPath  parent
     * @param  newFile     new file
     * @return true if added
     */
    public boolean add(TreePath parentPath, File newFile) {
        Object parent = parentPath.getLastPathComponent();
        if (parent == root && root.add(newFile)) {
            notifyChanged(parentPath);
            return true;
        } else if (parent != null) {
            List<File> children = childrenOfParent.get(parent);
            if (children != null && !children.contains(newFile)) {
                children.add(newFile);
                notifyChanged(parentPath);
                return true;
            }
        }
        return false;
    }
}
