package org.jphototagger.lib.componentutil;

import org.jphototagger.api.messages.MessageType;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Displays text in a {@code JLabel} for an amount of milliseconds, then hides
 * the text.
 *
 * @author Elmar Baumann
 */
public final class MessageLabel {
    private final JLabel label;

    public MessageLabel(JLabel label) {
        if (label == null) {
            throw new NullPointerException("label == null");
        }

        this.label = label;
    }

    public void showMessage(final String message, final MessageType type, final long milliseconds) {
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
                label.setForeground(type.isError()
                                    ? Color.RED
                                    : Color.BLACK);
                label.setText(message);

                Thread thread = new Thread(new HideInfoMessage(message, milliseconds),
                                           "JPhotoTagger: Hiding message popup");

                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
        });
    }

    private class HideInfoMessage implements Runnable {
        private final long milliseconds;
        private final String text;

        HideInfoMessage(String text, long milliseconds) {
            this.text = (text == null)
                        ? ""
                        : text;
            this.milliseconds = milliseconds;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }

            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    if (text.equals(label.getText())) {
                        label.setText("");
                    }
                }
            });
        }
    }
}
