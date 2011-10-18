package org.jphototagger.lib.lookup;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import org.jphototagger.api.nodes.Node;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ListUtil;

/**
 * List which puts selected list items into it's a Lookup.
 * <p>
 * If the (temporary) selected items are {@link Node}s, a popup menu with
 * it's actions is displayed.
 *
 * @author Elmar Baumann
 */
public final class LookupList extends JXList implements Lookup.Provider, MouseListener {

    private static final long serialVersionUID = 1L;
    private final InstanceContent selectionContent = new InstanceContent();
    private final Lookup selectionLookup = new AbstractLookup(selectionContent);
    private final InstanceContent temporarySelectionContent = new InstanceContent();
    private final Lookup temporarySelectionLookup = new AbstractLookup(temporarySelectionContent);
    private int lastRightClickIndex = -1;

    public LookupList() {
        super(new DefaultListModel());
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
        Collection<?> selectedContent = LookupUtil.createContentOfSelectedValues(this);

        if (!MouseEventUtil.isPopupTrigger(e)) {
            if (mouseCursorIndex != lastRightClickIndex) {
                lastRightClickIndex = mouseCursorIndex;
                selectionContent.set(selectedContent, null);
            }
            return;
        }

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
        // ignore
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
