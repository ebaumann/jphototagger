package de.elmar_baumann.lib.component;

import java.awt.event.KeyEvent;
import javax.swing.JTextArea;

/**
 * Textarea, die mit der Tabulatortaste verlassen wird.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-19
 */
public final class TabLeavingTextArea extends JTextArea {

    @Override
    protected void processComponentKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED &&
            e.getKeyCode() == KeyEvent.VK_TAB) {
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
}
