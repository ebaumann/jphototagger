package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Gespeicherte Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class TreeModelSavedSearches implements TreeModel {

    private Database db = Database.getInstance();
    private String root = Bundle.getString("TreeModelSavedSearches.RootItem");
    private List<SavedSearch> nodes = new ArrayList<SavedSearch>();
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    private enum ActionType {

        nodeInserted, nodeRenamed, nodeRemoved
    };

    public TreeModelSavedSearches() {
        addSavedSearches();
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
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    private void notifyListener(TreeModelEvent evt, ActionType type) {
        for (TreeModelListener l : listeners) {
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
     * Fügt eine gespeicherte Suche hinzu.
     * 
     * @param node Suche
     */
    public void addNode(SavedSearch node) {
        int index = nodes.indexOf(node);
        if (index >= 0) {
            nodes.set(index, node);
        } else {
            nodes.add(node);
        }
        notifyListener(createTreeModelEvent(nodes.indexOf(node), node), ActionType.nodeInserted);
    }

    /**
     * Benennt eine gespeicherte Suche (um).
     * 
     * @param oldNode Gespeicherte Suche, die umbenannt werden soll
     * @param newNode Umbenannte gespeicherte Suche
     */
    public void renameNode(SavedSearch oldNode, SavedSearch newNode) {
        int index = nodes.indexOf(oldNode);
        nodes.set(index, newNode);
        notifyListener(createTreeModelEvent(index, newNode), ActionType.nodeRenamed);
    }

    /**
     * Entfernt eine gespeicherte Suche.
     * 
     * @param node Zu entfernende Suche
     */
    public void removeNode(SavedSearch node) {
        int index = nodes.indexOf(node);
        SavedSearch n = nodes.get(index);
        nodes.remove(index);
        notifyListener(createTreeModelEvent(index, n), ActionType.nodeRemoved);
    }

    private TreeModelEvent createTreeModelEvent(int index, SavedSearch node) {
        return new TreeModelEvent(
            this,
            new Object[]{root},
            new int[]{index},
            new Object[]{node});
    }

    private void addSavedSearches() {
        List<SavedSearch> dbNodes = db.getSavedSearches();
        for (SavedSearch node : dbNodes) {
            addNode(node);
        }
    }
}
