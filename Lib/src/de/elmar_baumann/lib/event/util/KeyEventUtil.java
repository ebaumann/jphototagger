/*
 * @(#)KeyEventUtil.java    Created on 2008-10-26
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

package de.elmar_baumann.lib.event.util;

import java.awt.event.KeyEvent;

/**
 *
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class KeyEventUtil {

    /**
     * Returns whether to copy, i.e. that the key combination
     * <code>Ctrl+C</code> is pressed.
     *
     * @param  e  key event
     * @return true if insert
     */
    public static boolean isCopy(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == KeyEvent.VK_C)
               && (e.getModifiers() == KeyEvent.CTRL_MASK);
    }

    /**
     * Returns whether to cut, i.e. that the key combination
     * <code>Ctrl+X</code> is pressed.
     *
     * @param  e  key event
     * @return true if insert
     */
    public static boolean isCut(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == KeyEvent.VK_X)
               && (e.getModifiers() == KeyEvent.CTRL_MASK);
    }

    /**
     * Returns whether to paste, i.e. that the key combination
     * <code>Ctrl+V</code> is pressed.
     *
     * @param  e  key event
     * @return true if insert
     */
    public static boolean isPaste(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == KeyEvent.VK_V)
               && (e.getModifiers() == KeyEvent.CTRL_MASK);
    }

    /**
     * Returns whether a specific key is pressed in combination with the control
     * key.
     *
     * @param  e       key event
     * @param keyCode  key code
     * @return         true that key plus control are both pressed
     */
    public static boolean isControl(KeyEvent e, int keyCode) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == keyCode)
               && (e.getModifiers() == KeyEvent.CTRL_MASK);
    }

    /**
     * Returns whether a specific key is pressed in combination with the control
     * and alt key.
     *
     * @param  e       key event
     * @param keyCode  key code
     * @return         true that key plus control are both pressed
     */
    public static boolean isControlAlt(KeyEvent e, int keyCode) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == keyCode)
               && (e.getModifiers()
                   == (KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    }

    /**
     * Returns whether the Shift key was down.
     *
     * Motivation: {@link KeyEvent#getModifiers()} does not always return
     * {@link KeyEvent#SHIFT_DOWN_MASK} when the Shift key was down so that
     * also {@link KeyEvent#SHIFT_MASK} has to be compared against the modifiers
     * return value.
     *
     * @param  e key event
     * @return   true if the Shift key was down
     */
    public static boolean isShiftDown(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getModifiers() == KeyEvent.SHIFT_MASK)
               || (e.getModifiers() == KeyEvent.SHIFT_DOWN_MASK);
    }

    private KeyEventUtil() {}
}
