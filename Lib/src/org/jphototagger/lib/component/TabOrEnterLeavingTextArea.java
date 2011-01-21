package org.jphototagger.lib.component;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

/**
 * Text area where the tab or enter key transferring the focus.
 *
 * This text area is thought as a replacement for a text field but with the
 * capabilities of a text area.
 *
 * @author Elmar Baumann
 */
public final class TabOrEnterLeavingTextArea extends JTextArea {
    private static final long serialVersionUID = -6104921627665799043L;

    @Override
    protected void processComponentKeyEvent(KeyEvent evt) {
        if ((evt.getID() == KeyEvent.KEY_PRESSED) && isLeave(evt.getKeyCode())) {
            evt.consume();

            if (evt.isShiftDown()) {
                transferFocusBackward();
            } else {
                transferFocus();
            }
        } else {
            super.processComponentKeyEvent(evt);
        }
    }

    private static boolean isLeave(int keyCode) {
        return (keyCode == KeyEvent.VK_TAB) || (keyCode == KeyEvent.VK_ENTER);
    }
}
