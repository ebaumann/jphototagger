/*
 * @(#)PopupMenuList.java    Created on 2010-22-03
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.event.listener;

import org.jphototagger.lib.componentutil.ListItemTempSelectionRowSetter;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * Popup menu listening to a {@link JList} and handling temporary selections:
 * Right mouse clicks over a not selected list item.
 * <p>
 * If the click is a popup trigger the popup menu will be displayed.
 * <p>
 * Renders the temporary selection: If clicked on a selection, the rendering
 * does not change, all selected list items remain selected. If clicked outside
 * a selected list item, the current selection seems to change to the list item
 * under the mouse cursor and the previos selected list item seeming to be
 * deselected, but this is only rendered in that way.
 * <p>
 * {@link Listener}s are getting the rendered selection rather than the
 * "real" list selection.
 *
 * @author  Elmar Baumann
 */
public abstract class PopupMenuList extends JPopupMenu
        implements ActionListener, MouseListener {
    private static final long                          serialVersionUID =
        378844650671466081L;
    private final Map<JMenuItem, Collection<Listener>> listenersOfItem =
        new HashMap<JMenuItem, Collection<Listener>>();
    private List<Object> lastSelValues;
    private final JList  list;

    /**
     * Creates a new instance.
     *
     * @param list list where the popup menu will be shown
     */
    protected PopupMenuList(JList list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        this.list = list;
        addMenuItems();
        listenToMenuItems(this);
        list.addMouseListener(this);
        new ListItemTempSelectionRowSetter(list, this);
    }

    /**
     * Adds the menu items.
     * <p>
     * Called within the constructor. Specialized classes are creating and
     * adding their items within this method call.
     */
    protected abstract void addMenuItems();

    private void listenToMenuItems(MenuElement menuElement) {
        if (menuElement instanceof JMenuItem) {
            ((JMenuItem) menuElement).addActionListener(this);
        }

        for (MenuElement subMenuElement : menuElement.getSubElements()) {
            listenToMenuItems(subMenuElement);    // recursive
        }
    }

    /**
     * Adds a listener for a specific menu item.
     *
     * @param menuItem menu item
     * @param listener listener for that item; will be called if that item
     *                 was selected, see {@link Listener#action(JList, List)}
     */
    public void addListener(JMenuItem menuItem, Listener listener) {
        if (menuItem == null) {
            throw new NullPointerException("menuItem == null");
        }

        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (listenersOfItem) {
            Collection<Listener> listeners = listenersOfItem.get(menuItem);

            if (listeners == null) {
                listeners = new HashSet<Listener>();
                listenersOfItem.put(menuItem, listeners);
            }

            listeners.add(listener);
        }
    }

    /**
     * Removes an added listener.
     *
     * @param menuItem menu item
     * @param listener listener added via
     *                 {@link #addListener(JMenuItem, PopupMenuList.Listener)}
     */
    public void removeListener(JMenuItem menuItem, Listener listener) {
        if (menuItem == null) {
            throw new NullPointerException("menuItem == null");
        }

        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (listenersOfItem) {
            Collection<Listener> listeners = listenersOfItem.get(menuItem);

            if (listeners != null) {
                listeners.remove(listener);

                if (listeners.isEmpty()) {
                    listenersOfItem.remove(menuItem);
                }
            }
        }
    }

    private void notifyListeners(JMenuItem menuItem) {
        synchronized (listenersOfItem) {
            List<Object> paths = new ArrayList<Object>(lastSelValues);

            for (Listener listener : listenersOfItem.get(menuItem)) {
                listener.action(list, paths);
            }
        }
    }

    private void showPopupMenu(MouseEvent evt) {
        if (setLastSelValues(evt)) {
            setMenuItemsEnabled(new ArrayList<Object>(lastSelValues));
            show(list, evt.getX(), evt.getY());
        }
    }

    private boolean setLastSelValues(MouseEvent evt) {
        int mouseCursorIndex = ListUtil.getItemIndex(evt);

        if (mouseCursorIndex < 0) {
            return false;
        }

        if (list.isSelectedIndex(mouseCursorIndex)) {
            setAllSelectedListItems();
        } else {
            lastSelValues = Collections.singletonList(list.getSelectedValue());
        }

        return true;
    }

    private void setAllSelectedListItems() {
        lastSelValues = Arrays.asList(list.getSelectedValues());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) source;

            if (listenersOfItem.containsKey(menuItem)) {
                notifyListeners(menuItem);
            }
        }
    }

    /**
     * Called before the popup menu will be displayed.
     * <p>
     * Specialized classes can enable their menu items depending on the
     * selected list values.
     *
     * @param selListValues selected list values
     */
    protected void setMenuItemsEnabled(List<Object> selListValues) {

        // do nothing
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (!MouseEventUtil.isPopupTrigger(evt)) {
            return;
        }

        if (evt.getSource() == list) {
            showPopupMenu(evt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseReleased(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseExited(MouseEvent evt) {

        // ignore
    }

    public interface Listener {

        /**
         * Called if an item of this menu was selected.
         *
         * @param list           list
         * @param selectedValues selected list values
         */
        void action(JList list, List<Object> selectedValues);
    }
}
