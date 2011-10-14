package org.jphototagger.program.app.ui;

import org.jphototagger.lib.swing.GlassPaneWaitCursor;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
final class WaitDisplay {

    private final GlassPaneWaitCursor waitCursor;
    static final WaitDisplay INSTANCE = new WaitDisplay();

    void show() {
        synchronized (waitCursor) {
            waitCursor.show();
        }
    }

    void hide() {
        synchronized (waitCursor) {
            waitCursor.hide();
        }
    }

    boolean isShow() {
        synchronized (waitCursor) {
            return waitCursor.isShow();
        }
    }

    WaitDisplay() {
        waitCursor = new GlassPaneWaitCursor(GUI.getAppFrame());
    }
}
