package de.elmar_baumann.imv.event;

/**
 * Fehler.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-03
 */
public final class ErrorEvent {

    private final String message;
    private final Object source;

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
