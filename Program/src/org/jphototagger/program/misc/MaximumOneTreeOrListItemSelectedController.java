package org.jphototagger.program.misc;

import java.util.Arrays;
import java.util.List;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.api.windows.SelectionItemSelectedEvent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class MaximumOneTreeOrListItemSelectedController implements TreeSelectionListener, ListSelectionListener {

    private boolean listen = true;
    private final List<JXList> selectionLists = GUI.getAppPanel().getSelectionLists();
    private final List<JTree> selectionTrees = GUI.getAppPanel().getSelectionTrees();

    public MaximumOneTreeOrListItemSelectedController() {
        listen();
    }

    private void listen() {
        for (JTree tree : selectionTrees) {
            tree.addTreeSelectionListener(this);
        }

        for (JXList list : selectionLists) {
            list.addListSelectionListener(this);
        }

        AnnotationProcessor.process(this);
    }

    // Only while migration to modules, this class will be obsolete if all
    // Lists and all trees are located within modules
    @EventSubscriber(eventClass = SelectionItemSelectedEvent.class)
    public void sectionItemSelected(SelectionItemSelectedEvent evt) {
        Object source = evt.getSource();
        boolean sourceIsKnownList = false;
        boolean sourceIsKnownTree = false;
        if (source instanceof JXList) {
            sourceIsKnownList = selectionLists.contains((JXList) source);
        }
        if (source instanceof JXTree) {
            sourceIsKnownTree = selectionTrees.contains((JXTree) source);
        }
        if (!sourceIsKnownList && !sourceIsKnownTree) {
            clearSelectionAllTrees();
            clearSelectionAllLists();
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

        if (listen && !evt.getValueIsAdjusting() && (o instanceof JXList)) {
            JXList list = (JXList) o;

            if (list.getSelectedIndex() >= 0) {
                handleListSelected(list);
            }
        }
    }

    private void handleTreeSelected(JTree currentSelectedTree) {
        EventBus.publish(new SelectionItemSelectedEvent(currentSelectedTree, Arrays.asList(currentSelectedTree.getSelectionPaths())));
        clearSelectionAllLists();
        clearSelectionOtherTrees(currentSelectedTree);
    }

    private void handleListSelected(JXList currentSelectedList) {
        EventBus.publish(new SelectionItemSelectedEvent(currentSelectedList, Arrays.asList(currentSelectedList.getSelectedValues())));
        clearSelectionAllTrees();
        clearSelectionOtherLists(currentSelectedList);
    }

    private void clearSelectionOtherLists(JXList list) {
        listen = false;

        for (JXList aList : GUI.getAppPanel().getSelectionLists()) {
            if ((aList != list) && !aList.isSelectionEmpty()) {
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

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(ThumbnailsChangedEvent evt) {
        OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();

        if (OriginOfDisplayedThumbnails.UNDEFINED_ORIGIN.equals(origin)) {
            clearSelectionAllTrees();
            clearSelectionAllLists();
        }
    }
}
