package de.elmar_baumann.imv.controller;

/**
 * Basisklasse der Controller. <strong>Spezialisierte Klassen sollten
 * Benutzereingaben nur auswerten, wenn der Zustand gilt:
 * {@link #isControl()} .</strong>
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class Controller {

    private boolean control = true;

    protected Controller() {
    }

    public boolean isControl() {
        return control;
    }

    synchronized public void setControl(boolean control) {
        this.control = control;
    }
}
