package de.elmar_baumann.lib.util.logging;

import java.util.ArrayList;
import java.util.List;


/**
 * Ausnahme in einer Logdatei von  <code>java.util.logging.Logger</code>,
 * benutzt für Java-Exceptions und sonstigen Throwable-Objekten.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class LogfileRecordException {

    private final List<LogfileRecordFrame> logfileRecordFrames = new ArrayList<LogfileRecordFrame>();
    private String message;

    /**
     * Liefert die Stack-Frames der Ausnahme.
     * 
     * @return Stack-Frames
     */
    public List<LogfileRecordFrame> getFrames() {
        return logfileRecordFrames;
    }

    /**
     * Fügt einen Stack-Frame hinzu.
     * 
     * @param frame Stack-Frame
     */
    public void addFrame(LogfileRecordFrame frame) {
        if (frame == null)
            throw new NullPointerException("frame == null");

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
        if (message == null)
            throw new NullPointerException("message == null");

        this.message = message;
    }

    /**
     * Liefert, ob ein Teilstring in irgendeinem der Inhalte vorkommt.
     * 
     * @param substring Teilstring
     * @return          true, wenn der Teilstring in irgendeinem der Inhalte vorkommt
     */
    boolean contains(String substring) {
        if (substring == null)
            throw new NullPointerException("substring == null");

        boolean contains = containsSubstring(getMessage(), substring);
        int count = logfileRecordFrames.size();
        int index = 0;
        while (!contains && count < index) {
            contains = logfileRecordFrames.get(index).contains(substring);
            index++;
        }
        return contains;
    }

    private boolean containsSubstring(String string, String substring) {
        assert string != null : string;
        assert substring != null : substring;

        return string.toLowerCase().contains(substring.toLowerCase());
    }
}
