package org.jphototagger.program.controller.misc;

import org.jphototagger.program.resource.GUI;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jdesktop.swingx.JXList;
import javax.swing.JTree;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerItemsMutualExcludeSelection implements TreeSelectionListener, ListSelectionListener {
    private boolean listen = true;

    public ControllerItemsMutualExcludeSelection() {
        listen();
    }

    private void listen() {
        for (JTree tree : GUI.getAppPanel().getSelectionTrees()) {
            tree.addTreeSelectionListener(this);
        }

        for (JXList list : GUI.getAppPanel().getSelectionLists()) {
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

        if (listen &&!evt.getValueIsAdjusting() && (o instanceof JXList)) {
            JXList list = (JXList) o;

            if (list.getSelectedIndex() >= 0) {
                handleListSelected(list);
            }
        }
    }

    private void handleTreeSelected(JTree currentSelectedTree) {
        clearSelectionAllLists();
        clearSelectionOtherTrees(currentSelectedTree);
    }

    private void handleListSelected(JXList currentSelectedList) {
        clearSelectionAllTrees();
        clearSelectionOtherLists(currentSelectedList);
    }

    private void clearSelectionOtherLists(JXList list) {
        listen = false;

        for (JXList aList : GUI.getAppPanel().getSelectionLists()) {
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
        for (JXList list : GUI.getAppPanel().getSelectionLists()) {
            list.clearSelection();
        }
    }
}
