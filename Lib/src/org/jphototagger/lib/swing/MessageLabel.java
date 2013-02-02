package org.jphototagger.lib.swing;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Displays text in a {@code JLabel} for an amount of milliseconds, then hides
 * the text.
 *
 * @author Elmar Baumann
 */
public final class MessageLabel {

    private final JLabel label;
    private long removeTextTimeMillis;
    private Thread currentHideTextThread;
    private final Object monitor = new Object();

    public MessageLabel(JLabel label) {
        if (label == null) {
            throw new NullPointerException("label == null");
        }

        this.label = label;
    }

    public void showMessage(final String message, final MessageType type, long milliseconds) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        if (type == null) {
            throw new NullPointerException("type == null");
        }

        if (milliseconds < 0) {
            throw new IllegalArgumentException("Negative milliseconds: " + milliseconds);
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                label.setForeground(getForegroundColorOfMessageType(type));
                label.setText(message);
            }
        });

        synchronized (monitor) {
            if (currentHideTextThread != null) {
                return;
            }

            removeTextTimeMillis = System.currentTimeMillis() + milliseconds;
            currentHideTextThread = new HideTextThread();
            currentHideTextThread.start();
        }
    }

    private Color getForegroundColorOfMessageType(MessageType type) {
        return type.isError()
                ? Color.RED
                : Color.BLACK;
    }

    private class HideTextThread extends Thread {

        private HideTextThread() {
            super("JPhotoTagger: Hiding message label text");
            setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            long sleepTime = 0;
            synchronized (monitor) {
                sleepTime = removeTextTimeMillis - currentTimeMillis;
            }
            while (sleepTime > 0 && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(sleepTime);
                } catch (Throwable t) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, t);
                }
                currentTimeMillis = System.currentTimeMillis();
                synchronized (monitor) {
                    sleepTime = removeTextTimeMillis - currentTimeMillis;
                }
            }

            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    label.setText("");

                    synchronized (monitor) {
                        currentHideTextThread = null;
                    }
                }
            });
        }
    }
}
