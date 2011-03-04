package org.jphototagger.lib.util.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ausnahme in einer Logdatei von  <code>java.util.logging.Logger</code>,
 * benutzt für Java-Exceptions und sonstigen Throwable-Objekten.
 *
 * @author Elmar Baumann
 */
public final class ExceptionLogfileRecord {
    private final List<FrameLogfileRecord> logfileRecordFrames = new ArrayList<FrameLogfileRecord>();
    private String message;

    /**
     * Liefert die Stack-Frames der Ausnahme.
     *
     * @return Stack-Frames
     */
    public List<FrameLogfileRecord> getFrames() {
        return Collections.unmodifiableList(logfileRecordFrames);
    }

    /**
     * Fügt einen Stack-Frame hinzu.
     *
     * @param  frame Stack-Frame
     * @throws NullPointerException if frame is null
     */
    public void addFrame(FrameLogfileRecord frame) {
        if (frame == null) {
            throw new NullPointerException("frame == null");
        }

        logfileRecordFrames.add(frame);
    }

    /**
     * Liefert die Nachricht der Ausnahme.
     *
     * @return Nachricht oder null, falls nicht existent
     * @see    #hasMessage()
     */
    public String getMessage() {
        return message;
    }

    /**
     * Liefert, ob eine Nachricht existiert.
     *
     * @return true, falls existent
     */
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * Setzt die Nachricht der Ausnahme.
     *
     * @param message Nachricht
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Liefert, ob ein Teilstring in irgendeinem der Inhalte vorkommt.
     *
     * @param  substring  Teilstring
     * @return true, wenn der Teilstring in irgendeinem der Inhalte vorkommt
     * @throws NullPointerException if substring is null
     */
    boolean contains(String substring) {
        if (substring == null) {
            throw new NullPointerException("substring == null");
        }

        boolean contains = containsSubstring(getMessage(), substring);
        int count = logfileRecordFrames.size();
        int index = 0;

        while (!contains && (count < index)) {
            contains = logfileRecordFrames.get(index).contains(substring);
            index++;
        }

        return contains;
    }

    private boolean containsSubstring(String string, String substring) {
        if (string == null) {
            return false;
        }

        return string.toLowerCase().contains(substring.toLowerCase());
    }
}
