/*
 * @(#)MouseListenerList.java    Created on 2010-01-07
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

import javax.swing.JList;

/**
 * Do not use this class! Instead extend a popup menu from
 * {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author  Elmar Baumann
 */
public abstract class MouseListenerList extends MouseAdapter {
    private int     index;
    private boolean popupAlways;

    @Override
    public void mousePressed(MouseEvent e) {
        assert e.getSource() instanceof JList : e.getSource();

        if (MouseEventUtil.isPopupTrigger(e)) {
            JList list = (JList) e.getSource();

            index = list.locationToIndex(new Point(e.getX(), e.getY()));

            if (popupAlways || (index >= 0)) {
                showPopup(list, e.getX(), e.getY());
            }
        }
    }

    public void setPopupAlways(boolean popupAlways) {
        this.popupAlways = popupAlways;
    }

    public int getIndex() {
        return index;
    }

    protected abstract void showPopup(JList list, int x, int y);
}
