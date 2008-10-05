package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import java.util.ArrayList;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 */
public class ControllerItemsMutualExcludeSelection extends Controller
    implements TreeSelectionListener, ListSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ArrayList<JTree> trees = appPanel.getSelectionTrees();
    private ArrayList<JList> lists = appPanel.getSelectionLists();
    private boolean listen = true;

    public ControllerItemsMutualExcludeSelection() {
        listenToActionSources();
    }

    private void listenToActionSources() {
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
        if (listen && isStarted() && o instanceof JTree) {
            JTree tree = (JTree) o;
            if (e.isAddedPath()) {
                deselectAllLists();
                deselectOtherTrees(tree);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object o = e.getSource();
        if (listen && isStarted() && o instanceof JList) {
            JList list = (JList) o;
            deselectAllTrees();
            deselectOtherLists(list);
        }
    }

    private void deselectOtherLists(JList list) {
        listen = false;
        for (JList aList : lists) {
            if (aList != list && !aList.isSelectionEmpty()) {
                aList.clearSelection();
            }
        }
        listen = true;
    }

    private void deselectOtherTrees(JTree tree) {
        listen = false;
        for (JTree aTree : trees) {
            if (aTree != tree && aTree.getSelectionCount() > 0) {
                aTree.clearSelection();
            }
        }
        listen = true;
    }

    private void deselectAllTrees() {
        for (JTree tree : trees) {
            tree.clearSelection();
        }
    }

    private void deselectAllLists() {
        for (JList list : lists) {
            list.clearSelection();
        }
    }
}
