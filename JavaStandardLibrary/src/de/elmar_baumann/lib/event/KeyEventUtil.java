package de.elmar_baumann.lib.event;

import java.awt.event.KeyEvent;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
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
        return e.getKeyCode() == KeyEvent.VK_X &&
            e.getModifiers() == KeyEvent.CTRL_MASK;
    }

    /**
     * Returns whether to insert, i.e. that the key combination 
     * <code>Ctrl+V</code> is pressed.
     * 
     * @param  e  key event
     * @return true if insert
     */
    public static boolean isInsert(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_V &&
            e.getModifiers() == KeyEvent.CTRL_MASK;
    }

    private KeyEventUtil() {
    }
}
