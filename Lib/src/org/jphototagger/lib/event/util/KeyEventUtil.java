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

package org.jphototagger.lib.event.util;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

import javax.swing.KeyStroke;

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
    private KeyEventUtil() {}

    /**
     * Returns whether to copy, i.e. that the key combination of
     * {@link #getMenuShortcutMask()} and the character <code>C</code>.
     *
     * @param  e    key event
     * @return true if copy
     */
    public static boolean isCopy(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == KeyEvent.VK_C)
               && (e.getModifiers() == getMenuShortcutMask());
    }

    /**
     * Returns whether to cut, i.e. that the key combination of
     * {@link #getMenuShortcutMask()} and the character <code>X</code>.
     *
     * @param  e    key event
     * @return true if cut
     */
    public static boolean isCut(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == KeyEvent.VK_X)
               && (e.getModifiers() == getMenuShortcutMask());
    }

    /**
     * Returns whether to paste, i.e. that the key combination of
     * {@link #getMenuShortcutMask()} and the character <code>V</code>.
     *
     * @param  e    key event
     * @return true if paste
     */
    public static boolean isPaste(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == KeyEvent.VK_V)
               && (e.getModifiers() == getMenuShortcutMask());
    }

    private static boolean isModifier(KeyEvent e, int modifier) {
        int modifiers = e.getModifiers();
        return (modifiers & modifier) == modifier;
    }

    private static boolean isKeyCode(KeyEvent e, int keyCode) {
        return e.getKeyCode() == keyCode;
    }


    /**
     * Returns, whether the key event is a menu shortcut: A key in combination
     * with {@link #getMenuShortcutMask()} and the ALT key down.
     *
     * @param e       key event
     * @param keyCode key code
     * @return        true if the event is a menu shortcut
     */
    public static boolean isMenuShortcutWithAlt(KeyEvent e, int keyCode) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }
        int menuShortcutMask = getMenuShortcutMask();

        return isKeyCode(e, keyCode)
               && (isModifier(e, menuShortcutMask | KeyEvent.ALT_DOWN_MASK)
               || isModifier(e, menuShortcutMask | KeyEvent.ALT_MASK));
    }

    /**
     * Returns, whether the key event is a menu shortcut: A key in combination
     * with {@link #getMenuShortcutMask()} and the shift key down.
     *
     * @param e       key event
     * @param keyCode key code
     * @return        true if the event is a menu shortcut
     */
    public static boolean isMenuShortcutWithShiftDown(KeyEvent e, int keyCode) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }
        int menuShortcutMask = getMenuShortcutMask();
        return isKeyCode(e, keyCode)
               && (isModifier(e, menuShortcutMask | KeyEvent.SHIFT_DOWN_MASK)
               || isModifier(e, menuShortcutMask | KeyEvent.SHIFT_MASK));
    }

    /**
     * Returns, whether the key event is a menu shortcut: A key in combination
     * with {@link #getMenuShortcutMask()}.
     *
     * @param e       key event
     * @param keyCode key code
     * @return        true if the event is a menu shortcut
     */
    public static boolean isMenuShortcut(KeyEvent e, int keyCode) {
        if (e == null) {
            throw new NullPointerException("e == null");
        }

        return (e.getKeyCode() == keyCode)
               && (e.getModifiers() == getMenuShortcutMask());
    }

    /**
     * The same as {@link Toolkit#getMenuShortcutKeyMask()}.
     *
     * @return mask
     */
    public static int getMenuShortcutMask() {
        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    }

    /**
     * Returns a key stroke without modifiers.
     *
     * @param  keyCode key code
     * @return         key stroke
     */
    public static KeyStroke getKeyStroke(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode, 0);
    }

    /**
     * Returns a key stroke with the modifier {@link #getMenuShortcutMask()}.
     *
     * @param  keyCode key code
     * @return         key stroke
     */
    public static KeyStroke getKeyStrokeMenuShortcut(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode, getMenuShortcutMask());
    }

    /**
     * Returns a key stroke with the modifiers {@link #getMenuShortcutMask()}
     * and ALT down.
     *
     * @param  keyCode key code
     * @return         key stroke
     */
    public static KeyStroke getKeyStrokeMenuShortcutWithAltDown(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode,
                                      getMenuShortcutMask()
                                      | InputEvent.ALT_DOWN_MASK);
    }

    /**
     * Returns a key stroke with the modifiers {@link #getMenuShortcutMask()}
     * and SHIFT down.
     *
     * @param  keyCode key code
     * @return         key stroke
     */
    public static KeyStroke getKeyStrokeMenuShortcutWithShiftDown(
            int keyCode) {
        return KeyStroke.getKeyStroke(keyCode,
                                      getMenuShortcutMask()
                                      | InputEvent.SHIFT_DOWN_MASK);
    }

    /**
     * Returns a key stroke with the modifiers {@link #getMenuShortcutMask()}
     * and ALT down.
     *
     * @param  keyCode key code
     * @return         key stroke
     */
    public static KeyStroke getKeyStrokeMenuShortcutWithShiftAltDown(
            int keyCode) {
        return KeyStroke.getKeyStroke(keyCode,
                                      getMenuShortcutMask()
                                      | InputEvent.SHIFT_DOWN_MASK
                                      | InputEvent.ALT_DOWN_MASK);
    }
}
