package de.elmar_baumann.lib.event.util;

import java.awt.event.KeyEvent;

/**
 * 
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
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
        if (e == null)
            throw new NullPointerException("e == null"); // NOI18N

        return e.getKeyCode() == KeyEvent.VK_C &&
                e.getModifiers() == KeyEvent.CTRL_MASK;
    }

    /**
     * Returns whether to cut, i.e. that the key combination 
     * <code>Ctrl+X</code> is pressed.
     * 
     * @param  e  key event
     * @return true if insert
     */
    public static boolean isCut(KeyEvent e) {
        if (e == null)
            throw new NullPointerException("e == null"); // NOI18N

        return e.getKeyCode() == KeyEvent.VK_X &&
                e.getModifiers() == KeyEvent.CTRL_MASK;
    }

    /**
     * Returns whether to paste, i.e. that the key combination
     * <code>Ctrl+V</code> is pressed.
     * 
     * @param  e  key event
     * @return true if insert
     */
    public static boolean isPaste(KeyEvent e) {
        if (e == null)
            throw new NullPointerException("e == null"); // NOI18N

        return e.getKeyCode() == KeyEvent.VK_V &&
                e.getModifiers() == KeyEvent.CTRL_MASK;
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
        if (e == null)
            throw new NullPointerException("e == null"); // NOI18N

        return e.getKeyCode() == keyCode &&
                e.getModifiers() == KeyEvent.CTRL_MASK;
    }

    private KeyEventUtil() {
    }
}
