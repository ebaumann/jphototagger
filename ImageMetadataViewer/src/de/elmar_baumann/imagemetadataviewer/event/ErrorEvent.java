package de.elmar_baumann.imagemetadataviewer.event;

/**
 * Fehler.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/03
 */
public class ErrorEvent {

    private String message;
    private Object source;

    /**
     * Konstruktor.
     * 
     * @param message Fehlernachricht
     * @param source  Fehlerquelle
     */
    public ErrorEvent(String message, Object source) {
        this.message = message;
        this.source = source;
    }

    /**
     * Liefert die Fehlernachricht.
     * 
     * @return Fehlernachricht
     */
    public String getMessage() {
        return message;
    }

    /**
     * Liefert die Fehlerquelle (Objekt, bei dem der Fehler auftrat).
     * 
     * @return Fehlerquelle
     */
    public Object getSource() {
        return source;
    }
}
