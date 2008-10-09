package de.elmar_baumann.imv.controller;

/**
 * Basisklasse der Controller. <strong>Spezialisierte Klassen sollten
 * Benutzereingaben nur auswerten, wenn der Zustand gilt:
 * {@link #isStarted()} .</strong>
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class Controller {

    private boolean isStarted = true;
    private boolean isStopped = false;

    /**
     * Konstruktor. Setzt den Zustand auf gestartet.
     */
    protected Controller() {
    }

    /**
     * Liefert, ob der Controller gestartet ist (auf Benutzereingaben reagiert).
     * 
     * @return true, wenn der Controller gestartet ist.
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Startet den Controller. Danach reagiert er auf Benutzereingaben.
     * Erst notwendig, wenn dieser vorher gestoppt wurde, da Default-Zustand.
     */
    synchronized public void start() {
        isStarted = true;
        isStopped = false;
    }

    /**
     * Liefert, ob der Controller <em>nicht</em> auf Benutzereingaben reagiert.
     * 
     * @return true, wenn der Controller <em>nicht</em> auf Benutzereingaben 
     *         reagiert
     */
    public boolean isStopped() {
        return isStopped;
    }

    /**
     * Stoppt den Controller, sodass er nicht mehr auf Benutzereingaben
     * reagiert.
     */
    synchronized public void stop() {
        isStopped = true;
        isStarted = false;
    }
}
