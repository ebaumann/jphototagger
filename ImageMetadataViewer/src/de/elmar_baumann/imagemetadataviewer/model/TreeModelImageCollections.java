package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class TreeModelImageCollections implements TreeModel {

    private String root = Bundle.getString("TreeModelImageCollections.RootItem");
    private Database db = Database.getInstance();
    private List<String> nodes = new ArrayList<String>();
    private List<TreeModelListener> listener = new ArrayList<TreeModelListener>();

    private enum ActionType {

        nodeInserted, nodeRenamed, nodeRemoved
    };

    public TreeModelImageCollections() {
        addCollections();
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == root && isValidIndex(index)) {
            return nodes.get(index);
        }
        return null;
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < nodes.size();
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == root) {
            return nodes.size();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node != root;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == root) {
            return nodes.indexOf(child);
        }
        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // Nichts tun
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listener.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listener.remove(l);
    }

    private void notifyListener(TreeModelEvent evt, ActionType type) {
        for (TreeModelListener l : listener) {
            if (type.equals(ActionType.nodeInserted)) {
                l.treeNodesInserted(evt);
            } else if (type.equals(ActionType.nodeRemoved)) {
                l.treeNodesRemoved(evt);
            } else if (type.equals(ActionType.nodeRenamed)) {
                l.treeNodesChanged(evt);
            }
        }
    }

    /**
     * Fügt eine Sammlung hinzu.
     * 
     * @param node Suche
     */
    public void addNode(String node) {
        int index = nodes.indexOf(node);
        if (index < 0) {
            nodes.add(node);
            notifyListener(createTreeModelEvent(nodes.indexOf(node), node), ActionType.nodeInserted);
        }
    }

    private TreeModelEvent createTreeModelEvent(int index, String node) {
        return new TreeModelEvent(
            this,
            new Object[]{root},
            new int[]{index},
            new Object[]{node});
    }

    /**
     * Benennt eine Sammlung um.
     * 
     * @param nameOldNode Bisheriger Name
     * @param nameNewNode Neuer Name
     */
    public void renameNode(String nameOldNode, String nameNewNode) {
        int index = nodes.indexOf(nameOldNode);
        nodes.set(index, nameNewNode);
        notifyListener(createTreeModelEvent(index, nameNewNode), ActionType.nodeRenamed);
    }

    /**
     * Entfernt eine Sammlung
     * 
     * @param node Sammlung
     */
    public void removeNode(String node) {
        int index = nodes.indexOf(node);
        nodes.remove(index);
        notifyListener(createTreeModelEvent(index, node), ActionType.nodeRemoved);
    }

    private void addCollections() {
        List<String> names = db.getImageCollectionNames();
        for (String name : names) {
            addNode(name);
        }
    }
}
