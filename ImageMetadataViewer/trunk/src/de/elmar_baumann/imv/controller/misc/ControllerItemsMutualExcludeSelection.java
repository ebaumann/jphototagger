package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.util.List;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Kontrolliert eine Gruppe von Lists und Trees, von denen nur bei einem Tree
 * oder einer List Items selektiert sein dürfen. Die Items der anderen werden
 * deselektiert, sobald bei einem ein Item in der Gruppe selektiert wird.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerItemsMutualExcludeSelection
        implements TreeSelectionListener, ListSelectionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final List<JTree> trees = appPanel.getSelectionTrees();
    private final List<JList> lists = appPanel.getSelectionLists();
    private boolean listen = true;

    public ControllerItemsMutualExcludeSelection() {
        listen();
    }

    private void listen() {
        for (JTree tree : trees) {
            tree.addTreeSelectionListener(this);
        }
        for (JList list : lists) {
            list.addListSelectionListener(this);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object o = e.getSource();
        if (listen && e.isAddedPath() && o instanceof JTree) {
            handleSelection((JTree) o);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object o = e.getSource();
        if (listen && o instanceof JList) {
            handleSelection((JList) o);
        }
    }

    private void handleSelection(JTree currentSelectedTree) {
        clearSelectionAllLists();
        clearSelectionOtherTrees(currentSelectedTree);
    }

    private void handleSelection(JList currentSelectedList) {
        clearSelectionAllTrees();
        clearSelectionOtherLists(currentSelectedList);
    }

    private void clearSelectionOtherLists(JList list) {
        listen = false;
        for (JList aList : lists) {
            if (aList != list && !aList.isSelectionEmpty()) {
                aList.clearSelection();
            }
        }
        listen = true;
    }

    private void clearSelectionOtherTrees(JTree tree) {
        listen = false;
        for (JTree aTree : trees) {
            if (aTree != tree && aTree.getSelectionCount() > 0) {
                aTree.clearSelection();
            }
        }
        listen = true;
    }

    private void clearSelectionAllTrees() {
        for (JTree tree : trees) {
            tree.clearSelection();
        }
    }

    private void clearSelectionAllLists() {
        for (JList list : lists) {
            list.clearSelection();
        }
    }
}
