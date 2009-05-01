package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.comparator.ComparatorFilesNames;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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

    private static final ComparatorFilesNames sortComparator = ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE;
    private final List<File> rootNodes = new ArrayList<File>();
    private final Map<File, List<File>> childrenOfNode = new HashMap<File, List<File>>();
    private final Object root = new Object();
    private final DirectoryFilter directoryFilter;

    public TreeModelDirectories(Set<DirectoryFilter.Option> options) {
        directoryFilter = new DirectoryFilter(options);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(root)) {
            if (rootNodes.size() == 0) {
                return addRootNodes();
            }
            return rootNodes.size();
        }
        List<File> children = childrenOfNode.get(parent);
        if (children == null) {
            return addSubdirectories((File) parent);
        } else {
            return children.size();
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        File file;
        if (parent.equals(root)) {
            file = rootNodes.get(index);
        } else {
            file = childrenOfNode.get(parent).get(index);
        }
        return file;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) <= 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(root)) {
            return rootNodes.indexOf(child);
        }
        return parent == null ||
            child == null ||
            childrenOfNode.get(parent) == null ? -1
            : childrenOfNode.get(parent).indexOf(child);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

    private int addRootNodes() {
        File[] roots = File.listRoots();
        TreePath rootDir = new TreePath(new Object[]{root});

        for (File dir : roots) {
            insertNode(rootDir, dir);
        }
        return rootNodes.size();
    }

    @SuppressWarnings("unchecked")
    private List<File> getSubDirectories(File file) {
        List<File> subDirectories = new ArrayList<File>();
        File[] fileList = file.listFiles(directoryFilter);

        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                subDirectories.add(fileList[i]);
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
        rootNodes.add(node);
        Collections.sort(rootNodes, sortComparator);
        int index = rootNodes.indexOf(node);
        childrenOfNode.put(node, null);
    }

    @SuppressWarnings("unchecked")
    public void insertChildNode(TreePath parentPath, File node) {
        Object parent = parentPath.getLastPathComponent();
        List<File> parentsChildren = childrenOfNode.get(parent);
        if (parentsChildren == null) {
            List<File> newChildren = new ArrayList<File>();
            newChildren.add(node);
            childrenOfNode.put((File) parent, newChildren);
            childrenOfNode.put(node, null);
        } else {
            boolean contains = false;
            contains = parentsChildren.contains(node);
            if (!contains) {
                parentsChildren.add(node);
                Collections.sort(parentsChildren, sortComparator);
                childrenOfNode.put(node, null);
            }
        }
    }

    public void removeNode(TreePath parentPath, File node) {
        removeChildrenOf(node);
        File parentFile = (File) parentPath.getLastPathComponent();
        List<File> parentsFiles = childrenOfNode.get(parentFile);
        if (parentsFiles != null) {
            parentsFiles.remove(node);
        }
        childrenOfNode.remove(node);
        rootNodes.remove(node);
    }

    private void removeChildrenOf(File node) {
        List<File> cachedFiles = getCachedFiles();
        String nodeName = node.getAbsolutePath() + File.separator;
        for (File cachedFile : cachedFiles) {
            if (cachedFile.getAbsolutePath().startsWith(nodeName)) {
                childrenOfNode.remove(cachedFile);
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

    private int addSubdirectories(File parent) {
        List<File> subdirectories = getSubDirectories(parent);
        TreePath parentPath = getTreePath(parent);
        for (File dir : subdirectories) {
            insertNode(parentPath, dir);
        }
        return subdirectories.size();
    }
}
