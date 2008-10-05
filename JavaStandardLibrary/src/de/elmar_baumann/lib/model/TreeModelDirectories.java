package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile.SortType;
import de.elmar_baumann.lib.io.DirectoryTreeModelRoots;
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
    private Map<DirectoryTreeModelFile, List<DirectoryTreeModelFile>> childrenOfParent = new HashMap<DirectoryTreeModelFile, List<DirectoryTreeModelFile>>();

    public TreeModelDirectories() {
        init();
    }

    private void init() {
        int count = root.getChildCount();
        for (int i = 0; i < count; i++) {
            DirectoryTreeModelFile child = root.getChild(i);
            childrenOfParent.put(child,
                child.getSubDirectories(SortType.ascendingNoCase));
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
        DirectoryTreeModelFile child = childrenOfParent.get(parent).get(index);
        if (!childrenOfParent.containsKey(child)) {
            childrenOfParent.put(child, child.getSubDirectories(SortType.ascendingNoCase));
        }
        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(root)) {
            return root.getChildCount();
        }
        List<DirectoryTreeModelFile> children = childrenOfParent.get(parent);
        if (children != null) {
            return children.size();
        }
        DirectoryTreeModelFile p = (DirectoryTreeModelFile) parent;
        List<DirectoryTreeModelFile> subdirectories = p.getSubDirectories(SortType.ascendingNoCase);
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
}
