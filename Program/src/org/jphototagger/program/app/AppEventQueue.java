package org.jphototagger.program.app;

import java.awt.AWTEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.program.app.ui.WaitDisplay;

/**
 * JPhotoTagger's event queue.
 * <p>
 * Catches throwables and displays a dialog with information about the cause.
 *
 * @author Elmar Baumann
 */
public final class AppEventQueue extends java.awt.EventQueue {

    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable t) {
            Logger.getLogger(AppEventQueue.class.getName()).log(Level.SEVERE, null, t);
            hideWaitDisplay();
        }
    }

    private void hideWaitDisplay() {
        if (WaitDisplay.INSTANCE.isShow()) {
            WaitDisplay.INSTANCE.hide();
        }
    }
}
