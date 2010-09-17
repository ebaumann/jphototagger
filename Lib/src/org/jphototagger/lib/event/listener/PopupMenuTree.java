/*
 * @(#)PopupMenuTree.java    Created on 2010-22-03
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

import org.jphototagger.lib.componentutil.TreeItemTempSelectionRowSetter;
import org.jphototagger.lib.componentutil.TreeUtil;
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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.MenuElement;
import javax.swing.tree.TreePath;

/**
 * Popup menu listening to a {@link JTree} and handling temporary selections:
 * Right mouse clicks over a not selected tree item.
 * <p>
 * If the click is a popup trigger the popup menu will be displayed.
 * <p>
 * Renders the temporary selection: If clicked on a selection, the rendering
 * does not change, all selected tree items remain selected. If clicked outside
 * a selected tree item, the current selection seems to change to the tree item
 * under the mouse cursor and the previos selected tree item seeming to be
 * deselected, but this is only rendered in that way.
 * <p>
 * {@link Listener}s are getting the rendered selection rather than the
 * "real" tree selection.
 *
 * @author Elmar Baumann
 */
public abstract class PopupMenuTree extends JPopupMenu
        implements ActionListener, MouseListener {
    private static final long                          serialVersionUID =
        378844650671466081L;
    private final Map<JMenuItem, Collection<Listener>> listenersOfItem =
        new HashMap<JMenuItem, Collection<Listener>>();
    private JMenuItem      itemCollapseExpandAllSubItems;
    private JMenuItem      itemExpandAllSubItems;
    private List<TreePath> lastSelTreePaths;
    private final JTree    tree;

    /**
     * Creates a new instance.
     *
     * @param tree tree where the popup menu will be shown
     */
    protected PopupMenuTree(JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        this.tree = tree;
        addMenuItems();
        listenToMenuItems(this);
        tree.addMouseListener(this);
        new TreeItemTempSelectionRowSetter(tree, this);
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
     *                 was selected, see {@link Listener#action(JTree, List)}
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
     *                 {@link #addListener(JMenuItem, PopupMenuTree.Listener)}
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
            List<TreePath> paths = new ArrayList<TreePath>(lastSelTreePaths);

            for (Listener listener : listenersOfItem.get(menuItem)) {
                listener.action(tree, paths);
            }
        }
    }

    /**
     * Sets a specific menu item for expanding all sub items of a selected
     * tree item: If that item will be selected, the subtree of the items
     * below the mouse cursor will be expanded recursively.
     *
     * @param item item
     */
    protected void setExpandAllSubItems(JMenuItem item) {
        if (item == null) {
            throw new NullPointerException("item == null");
        }

        itemExpandAllSubItems = item;
    }

    /**
     * Sets a specific menu item for collapsing all sub items of a selected
     * tree item: If that item will be selected, the subtree of the items
     * below the mouse cursor will be collapsed recursively.
     *
     * @param item item
     */
    protected void setCollapseAllSubItems(JMenuItem item) {
        if (item == null) {
            throw new NullPointerException("item == null");
        }

        itemCollapseExpandAllSubItems = item;
    }

    private void showPopupMenu(MouseEvent evt) {
        if (setLastSelTreePaths(evt)) {
            setMenuItemsEnabled(new ArrayList<TreePath>(lastSelTreePaths));
            show(tree, evt.getX(), evt.getY());
        }
    }

    private boolean setLastSelTreePaths(MouseEvent evt) {
        TreePath mouseCursorPath = TreeUtil.getTreePath(evt);

        if (mouseCursorPath == null) {
            return false;
        }

        if (tree.isPathSelected(mouseCursorPath)) {
            setAllSelectedTreePaths();
        } else {
            lastSelTreePaths = Collections.singletonList(mouseCursorPath);
        }

        return true;
    }

    private void setAllSelectedTreePaths() {
        lastSelTreePaths = Arrays.asList(tree.getSelectionPaths());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if ((source == itemExpandAllSubItems)) {
            TreeUtil.expandAll(tree, lastSelTreePaths, true);
        } else if ((source == itemCollapseExpandAllSubItems)) {
            TreeUtil.expandAll(tree, lastSelTreePaths, false);
        } else if (source instanceof JMenuItem) {
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
     * selected tree paths.
     *
     * @param selTreePaths selected treePaths
     */
    protected void setMenuItemsEnabled(List<TreePath> selTreePaths) {

        // ignore
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (!MouseEventUtil.isPopupTrigger(evt)) {
            return;
        }

        if (evt.getSource() == tree) {
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
         * @param tree              tree
         * @param selectedTreePaths selected paths
         */
        void action(JTree tree, List<TreePath> selectedTreePaths);
    }
}
