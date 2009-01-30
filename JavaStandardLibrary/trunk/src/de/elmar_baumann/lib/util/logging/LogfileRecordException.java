package de.elmar_baumann.lib.util.logging;

import java.util.ArrayList;
import java.util.List;


/**
 * Ausnahme in einer Logdatei von  <code>java.util.logging.Logger</code>,
 * benutzt für Java-Exceptions und sonstigen Throwable-Objekten.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class LogfileRecordException {

    private List<LogfileRecordFrame> logfileRecordFrames = new ArrayList<LogfileRecordFrame>();
    private String message;

    public LogfileRecordException() {
    }

    /**
     * Liefert die Stack-Frames der Ausnahme.
     * 
     * @return Stack-Frames
     */
    public List<LogfileRecordFrame> getFrames() {
        return logfileRecordFrames;
    }

    /**
     * Setzt die Stack-Frames der Ausnahme.
     * 
     * @param logfileRecordFrames Stack-Frames
     */
    public void setFrames(List<LogfileRecordFrame> logfileRecordFrames) {
        this.logfileRecordFrames = logfileRecordFrames;
    }

    /**
     * Fügt einen Stack-Frame hinzu.
     * 
     * @param frame Stack-Frame
     */
    public void addFrame(LogfileRecordFrame frame) {
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
     * @param substring Teilstring
     * @return          true, wenn der Teilstring in irgendeinem der Inhalte vorkommt
     */
    boolean contains(String substring) {
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
        return string == null
            ? false
            : string.toLowerCase().contains(substring.toLowerCase());
    }
}
