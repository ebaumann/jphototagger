package org.jphototagger.program.view;

import org.jphototagger.program.resource.GUI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.EventQueue;

/**
 * Displays on the (entire) application frame a wait symbol (currently a
 * wait cursor).
 *
 * @author Elmar Baumann
 */
public final class WaitDisplay {
    private static final MouseAdapter ma = new MouseAdapter() {}
    ;
    private static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
    private static volatile boolean mlAdded;
    private static volatile boolean isShow;
    private static final Object LOCK = new Object();

    /**
     * Shows the wait symbol (sets the wait cursor).
     */
    public static void show() {
        synchronized (LOCK) {
            addMouseListener();
        }

        show(true, WAIT_CURSOR);
    }

    /**
     * Hides the wait symbol (sets the default cursor).
     */
    public static void hide() {
        show(false, DEFAULT_CURSOR);
    }

    public static boolean isShow() {
        synchronized (LOCK) {
            return isShow;
        }
    }

    private static void show(final boolean show, final Cursor cursor) {
        final Component glassPane = GUI.getAppFrame().getGlassPane();

        if (EventQueue.isDispatchThread()) {
            synchronized (LOCK) {
                glassPane.setCursor(cursor);
                glassPane.setVisible(show);
                isShow = show;
            }
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    synchronized (LOCK) {
                        glassPane.setCursor(cursor);
                        glassPane.setVisible(show);
                        isShow = show;
                    }
                }
            });
        }
    }

    private static void addMouseListener() {
        if (!mlAdded) {
            mlAdded = true;
            GUI.getAppFrame().getGlassPane().addMouseListener(ma);
        }
    }

    private WaitDisplay() {}
}
