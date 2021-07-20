package org.jphototagger.program.app;

import java.awt.AWTEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.windows.WaitDisplayer;
import org.openide.util.Lookup;

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
        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        if (waitDisplayer != null && waitDisplayer.isShow()) {
            waitDisplayer.hide();
        }
    }
}
