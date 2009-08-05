package de.elmar_baumann.lib.component;

import java.awt.event.KeyEvent;
import javax.swing.JTextArea;

/**
 * Text area where the tab or enter key transferring the focus.
 *
 * This text area is thought as a replacement for a text field but with the
 * capabilities of a text area.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-19
 */
public final class TabOrEnterLeavingTextArea extends JTextArea {

    @Override
    protected void processComponentKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED && isLeave(e.getKeyCode())) {
            e.consume();
            if (e.isShiftDown()) {
                transferFocusBackward();
            } else {
                transferFocus();
            }
        } else {
            super.processComponentKeyEvent(e);
        }
    }

    private static boolean isLeave(int keyCode) {
        return keyCode == KeyEvent.VK_TAB || keyCode == KeyEvent.VK_ENTER;
    }
}
