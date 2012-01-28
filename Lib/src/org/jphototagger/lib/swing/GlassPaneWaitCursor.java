package org.jphototagger.lib.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;

import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Displays over a frame a wait wait cursor.
 *
 * @author Elmar Baumann
 */
public final class GlassPaneWaitCursor {

    private final MouseAdapter mouseAdapter = new MouseAdapter() {
    };
    private final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private final Cursor defaultCursor = Cursor.getDefaultCursor();
    private volatile boolean mouseListenerAdded;
    private volatile boolean isShow;
    private final Object monitor = new Object();
    private final JFrame frame;

    public GlassPaneWaitCursor(JFrame frame) {
        if (frame == null) {
            throw new NullPointerException("frame == null");
        }

        this.frame = frame;
    }

    public void show() {
        synchronized (monitor) {
            addMouseListener();
        }

        show(true, waitCursor);
    }

    public void hide() {
        show(false, defaultCursor);
    }

    public boolean isShow() {
        synchronized (monitor) {
            return isShow;
        }
    }

    private void show(final boolean show, final Cursor cursor) {

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                synchronized (monitor) {
                    Component glassPane = frame.getGlassPane();
                    glassPane.setCursor(cursor);
                    glassPane.setVisible(show);
                    isShow = show;
                }
            }
        });
    }

    private void addMouseListener() {
        if (!mouseListenerAdded) {
            mouseListenerAdded = true;
            Component glassPane = frame.getGlassPane();
            glassPane.addMouseListener(mouseAdapter);
        }
    }
}
