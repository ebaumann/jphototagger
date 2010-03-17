/*
 * @(#)ListItemTempSelectionRowSetter.java    2009-07-27
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

package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.event.util.MouseEventUtil;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import java.lang.reflect.Method;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

/**
 * Listens in a {@link JList} for popup triggers and sets to the list cell
 * renderer of that list the row index below the mouse location.
 *
 * If the popup menu becomes invisible, the row index will be set to -1.
 *
 * The cell renderer has to implement the method
 * <strong>setTempSelectionRow</strong> with an <strong>int</strong> as
 * parameter for the index.
 *
 * @author  Elmar Baumann
 */
public final class ListItemTempSelectionRowSetter
        implements MouseListener, PopupMenuListener {
    private final JList         list;
    private static final String TEMP_SEL_ROW_METHOD_NAME =
        "setTempSelectionRow";

    /**
     * Creates a new instance.
     *
     * @param list      list
     * @param popupMenu the list's popup menu
     */
    public ListItemTempSelectionRowSetter(JList list, JPopupMenu popupMenu) {
        this.list = list;
        list.addMouseListener(this);
        popupMenu.addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        setRowIndex(-1);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (MouseEventUtil.isPopupTrigger(e)) {
            int index = list.locationToIndex(new Point(e.getX(), e.getY()));

            if (index < 0) {
                return;
            }

            setRowIndex(index);
        }
    }

    private void setRowIndex(int index) {
        ListCellRenderer renderer = list.getCellRenderer();

        if (hasMethod(renderer)) {
            try {
                Method m =
                    renderer.getClass().getMethod(TEMP_SEL_ROW_METHOD_NAME,
                                                  int.class);

                m.invoke(renderer, index);
                list.repaint();
            } catch (Exception ex) {
                Logger.getLogger(
                    ListItemTempSelectionRowSetter.class.getName()).log(
                    Level.SEVERE, null, ex);
            }
        }
    }

    private boolean hasMethod(ListCellRenderer renderer) {
        for (Method method : renderer.getClass().getDeclaredMethods()) {
            if (method.getName().equals(TEMP_SEL_ROW_METHOD_NAME)) {
                Class<?>[] parameterTypes = method.getParameterTypes();

                if ((parameterTypes.length == 1)
                        && parameterTypes[0].equals(int.class)) {
                    return true;
                }
            }
        }

        return false;
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

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {

        // ignore
    }
}
