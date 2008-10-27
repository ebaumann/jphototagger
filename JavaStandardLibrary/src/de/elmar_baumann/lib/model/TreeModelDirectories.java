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
        // Nichts tun
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // Nichts tun
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // Nichts tun
    }

    @SuppressWarnings("unchecked")
    private List<File> getSubDirectories(File file, SortType sortType, boolean acceptHidden) {
        File[] listFiles = file.listFiles(new DirectoryFilter(acceptHidden));
        List<File> directories = new ArrayList<File>();

        if (listFiles != null) {
            for (int i = 0; listFiles != null && i < listFiles.length; i++) {
                directories.add(listFiles[i]);
            }
            Collections.sort(directories, new FileComparator(sortType));
        }
        return directories;
    }
}
