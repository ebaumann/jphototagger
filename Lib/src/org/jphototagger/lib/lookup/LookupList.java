package org.jphototagger.lib.lookup;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.nodes.Node;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ListUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * List which puts selected list items into it's a Lookup.
 * <p>
 * If the (temporary) selected items are {@link Node}s, a popup menu with
 * it's actions is displayed.
 * <p>
 * Double click's on a node perform the node's preferred action.
 *
 * @author Elmar Baumann
 */
public final class LookupList extends JXList implements Lookup.Provider, MouseListener {

    private static final long serialVersionUID = 1L;
    private final InstanceContent selectionContent = new InstanceContent();
    private final Lookup selectionLookup = new AbstractLookup(selectionContent);
    private final InstanceContent temporarySelectionContent = new InstanceContent();
    private final Lookup temporarySelectionLookup = new AbstractLookup(temporarySelectionContent);
    private int lastSelectionCount = 0;
    private int lastRightClickIndex = -1;

    public LookupList() {
        org.jphototagger.resources.UiFactory.configure(this);
        addMouseListener(this);
    }

    @Override
    public Lookup getLookup() {
        return selectionLookup;
    }

    /**
     * @return Lookup with the item's content if a right mouse click occured on a <em>not</em>
     * selected list item
     */
    public Lookup getTemporarySelectionLookup() {
        return temporarySelectionLookup;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseCursorIndex = ListUtil.getItemIndex(e);
        int currentSelectionCount = ListUtil.getSelectionCount(this);
        Collection<?> selectedContent = LookupUtil.createContentOfSelectedValues(this);

        if (!MouseEventUtil.isPopupTrigger(e)) {
            if (mouseCursorIndex != lastRightClickIndex || lastSelectionCount != currentSelectionCount) {
                lastRightClickIndex = mouseCursorIndex;
                selectionContent.set(selectedContent, null);
            }
            lastSelectionCount = currentSelectionCount;
            return;
        }

        lastSelectionCount = currentSelectionCount;
        if (isSelectedIndex(mouseCursorIndex)) {
            boolean isTemporarySelection = false;
            showPopupMenu(e.getX(), e.getY(), selectedContent, isTemporarySelection);
        } else {
            Object temporarySelectedValue = super.getElementAt(mouseCursorIndex);
            List<Object> temporarySelectedContent = Arrays.asList(temporarySelectedValue);
            temporarySelectionContent.set(temporarySelectedContent, null);
            boolean isTemporarySelection = true;
            showPopupMenu(e.getX(), e.getY(), temporarySelectedContent, isTemporarySelection);
            LookupUtil.clearInstanceContent(temporarySelectionContent);
        }
    }

    void showPopupMenu(int x, int y, Collection<?> selectedContent, boolean temporary) {
        JPopupMenu popupMenu = LookupUtil.createPopupMenuFromNodeActions(selectedContent, temporary);
        if (popupMenu != null) {
            popupMenu.show(this, x, y);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MouseEventUtil.isDoubleClick(e)) {
            int itemIndex = ListUtil.getItemIndex(e);
            if (itemIndex >= 0) {
                performPreferredActionAtItemIndex(itemIndex);
            }
        }
    }

    private void performPreferredActionAtItemIndex(int itemIndex) {
        Object elementAtIndex = super.getElementAt(itemIndex);
        if (elementAtIndex instanceof Node) {
            Node nodeAtIndex = (Node) elementAtIndex;
            performPreferredActionOfNode(nodeAtIndex);
        }
    }

    private void performPreferredActionOfNode(Node node) {
        Action preferredAction = node.getPreferredAction();
        if (preferredAction != null) {
            preferredAction.actionPerformed(new ActionEvent(this, 0, null));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ignore
    }
}
