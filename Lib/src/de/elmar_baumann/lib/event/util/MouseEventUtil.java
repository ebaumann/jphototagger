/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.lib.event.util;

import java.awt.event.MouseEvent;

/**
 *
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 * @version 2008-10-24
 */
public final class MouseEventUtil {

    /**
     * Workaround for some operating systems where
     * {@link java.awt.event.MouseEvent#isPopupTrigger()} does not return true
     * if the user clicked down the right mouse button.
     *
     * @param  e  mouse event
     * @return true if the right mouse button is down
     */
    public static boolean isPopupTrigger(MouseEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return e.isPopupTrigger() || (e.getModifiers() == 4);
    }

    /**
     * A more descriptive variant of
     * {@link java.awt.event.MouseEvent#getClickCount()} == 2.
     *
     * @param  e  mouse event
     * @return true, if the user clicked twice or more often
     */
    public static boolean isDoubleClick(MouseEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return e.getClickCount() >= 2;
    }

    /**
     * Returns, whether the left mouse button was clicked.
     *
     * @param  e  mouse event
     * @return true if the left mouse button was clicked
     */
    public static boolean isLeftClick(MouseEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return e.getButton() == MouseEvent.BUTTON1;
    }

    private MouseEventUtil() {}
}
