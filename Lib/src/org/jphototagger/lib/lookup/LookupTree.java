package org.jphototagger.lib.lookup;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import org.jphototagger.api.nodes.Node;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.TreeUtil;

/**
 * Tree which puts selected tree items into it's a Lookup.
 * <p>
 * If the (temporary) selected items are {@link Node}s, a popup menu with
 * it's actions is displayed.
 * <p>
 * Double click's on a node perform the node's preferred action.
 *
 * @author Elmar Baumann
 */
public final class LookupTree extends JXTree implements Lookup.Provider, MouseListener {

    private static final long serialVersionUID = 1L;
    private final InstanceContent selectionContent = new InstanceContent();
    private final Lookup selectionLookup = new AbstractLookup(selectionContent);
    private final InstanceContent temporarySelectionContent = new InstanceContent();
    private final Lookup temporarySelectionLookup = new AbstractLookup(temporarySelectionContent);
    private int lastSelectionCount = 0;
    private int lastRightClickRow = -1;

    public LookupTree() {
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
        int currentSelectionCount = getSelectionCount();
        Collection<?> selectedContent = LookupUtil.createContentOfSelectedValues(this);
        int rightClickRow = getRowForLocation(e.getX(), e.getY());

        if (!MouseEventUtil.isPopupTrigger(e)) {
            if (rightClickRow != lastRightClickRow || lastSelectionCount != currentSelectionCount) {
                lastRightClickRow = rightClickRow;
                selectionContent.set(selectedContent, null);
            }
            lastSelectionCount = currentSelectionCount;
            return;
        }

        lastSelectionCount = currentSelectionCount;
        if (isRowSelected(rightClickRow)) {
            boolean isTemporarySelection = false;
            showPopupMenu(e.getX(), e.getY(), selectedContent, isTemporarySelection);
        } else {
            Object temporarySelectedValue = LookupUtil.getTreeContentAtRow(this, rightClickRow);
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
            TreePath treePath = TreeUtil.getTreePath(e);
            Object lastPathComponent = treePath.getLastPathComponent();
            if (lastPathComponent instanceof Node) {
                Node node = (Node) lastPathComponent;
                performPreferredActionOfNode(node);
            }
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
