package org.jphototagger.program.controller.misc;

import org.jphototagger.program.resource.GUI;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JList;
import javax.swing.JTree;

/**
 * Kontrolliert eine Gruppe von Lists und Trees, von denen nur bei einem Tree
 * oder einer List Items selektiert sein dürfen. Die Items der anderen werden
 * deselektiert, sobald bei einem ein Item in der Gruppe selektiert wird.
 *
 * @author Elmar Baumann
 */
public final class ControllerItemsMutualExcludeSelection
        implements TreeSelectionListener, ListSelectionListener {
    private boolean listen = true;

    public ControllerItemsMutualExcludeSelection() {
        listen();
    }

    private void listen() {
        for (JTree tree : GUI.getAppPanel().getSelectionTrees()) {
            tree.addTreeSelectionListener(this);
        }

        for (JList list : GUI.getAppPanel().getSelectionLists()) {
            list.addListSelectionListener(this);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        Object o = evt.getSource();

        if (listen && evt.isAddedPath() && (o instanceof JTree)) {
            handleTreeSelected((JTree) o);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        Object o = evt.getSource();

        if (listen &&!evt.getValueIsAdjusting() && (o instanceof JList)) {
            JList list = (JList) o;

            if (list.getSelectedIndex() >= 0) {
                handleListSelected(list);
            }
        }
    }

    private void handleTreeSelected(JTree currentSelectedTree) {
        clearSelectionAllLists();
        clearSelectionOtherTrees(currentSelectedTree);
    }

    private void handleListSelected(JList currentSelectedList) {
        clearSelectionAllTrees();
        clearSelectionOtherLists(currentSelectedList);
    }

    private void clearSelectionOtherLists(JList list) {
        listen = false;

        for (JList aList : GUI.getAppPanel().getSelectionLists()) {
            if ((aList != list) &&!aList.isSelectionEmpty()) {
                aList.clearSelection();
            }
        }

        listen = true;
    }

    private void clearSelectionOtherTrees(JTree tree) {
        listen = false;

        for (JTree aTree : GUI.getAppPanel().getSelectionTrees()) {
            if ((aTree != tree) && (aTree.getSelectionCount() > 0)) {
                aTree.clearSelection();
            }
        }

        listen = true;
    }

    private void clearSelectionAllTrees() {
        for (JTree tree : GUI.getAppPanel().getSelectionTrees()) {
            tree.clearSelection();
        }
    }

    private void clearSelectionAllLists() {
        for (JList list : GUI.getAppPanel().getSelectionLists()) {
            list.clearSelection();
        }
    }
}
