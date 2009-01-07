package de.elmar_baumann.imv.event;

/**
 * Fortschritts-Ereignis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ProgressEvent {

    private Object source;
    private int maximum;
    private int minimum;
    private boolean indeterminate = false;
    private int value;
    private long milliSecondsRemaining = -1;
    private Object info;
    private boolean stop = false;

    /**
     * Konstruktor.
     * 
     * @param source  Quelle des Ereignisses
     * @param minimum Minimale Ereignisanzahl
     * @param maximum Maximale  Ereignisanzahl
     * @param value   Aktuelles Ereignis (Wert zwischen minimaler und maximaler
     *                Ereignisanzahl)
     * @param info    Beliebige Information
     */
    public ProgressEvent(Object source, int minimum, int maximum, int value,
        Object info) {
        this.source = source;
        this.minimum = minimum;
        this.maximum = maximum;
        this.value = value;
        this.info = info;
    }

    /**
     * Liefert die Ereignisquelle.
     * 
     * @return Ereignisquelle
     */
    public Object getSource() {
        return source;
    }

    /**
     * Setzt die Ereignisquelle.
     * 
     * @param source Ereignisquelle
     */
    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * Liefert die minimale Ereignisanzahl.
     * 
     * @return Minimale Ereignisanzahl
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * Setzt die minimale Ereignisanzahl.
     * 
     * @param minimum Minimale Ereignisanahl
     */
    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    /**
     * Liefert die maximale Ereignisanzahl.
     * 
     * @return Maximale Ereignisanzahl
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * Setzt die maximale Ereignisanzahl.
     * 
     * @param maximum Maximale Ereignisanzahl
     */
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    /**
     * Liefert die Anzahl aktuell verarbeiteter Ereignisse.
     * 
     * @return Aktuelle Ereignisanzahl
     */
    public int getValue() {
        return value;
    }

    /**
     * Setzt die Anzahl aktuell verarbeiteter Ereignisse.
     * 
     * @param value Anzahl
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Liefert die Information zum aktuellen Ereignis.
     * 
     * @return Information
     */
    public Object getInfo() {
        return info;
    }

    /**
     * Setzt die Information zum aktuellen Ereignis.
     * 
     * @param info Information
     */
    public void setInfo(Object info) {
        this.info = info;
    }

    /**
     * Liefert die noch verbleibenden Millisekunden bis zum Abschluss.
     * 
     * @return Verbleibende Millisekunden oder -1, wenn diese nicht berechnet wurden
     */
    public long getMilliSecondsRemaining() {
        return milliSecondsRemaining;
    }

    /**
     * Setzt die noch verbleibenden Millisekunden bis zum Abschluss.
     * 
     * @param milliSecondsRemaining Verbleibende Millisekunden
     */
    public void setMilliSecondsRemaining(long milliSecondsRemaining) {
        this.milliSecondsRemaining = milliSecondsRemaining;
    }

    /**
     * Teilt der Quelle mit, ob die Aktion abgebrochen werden soll.
     * 
     * @param stop true, wenn die Aktion abgebrochen werden soll.
     *              Default: false
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * Liefert, ob die Quelle die Aktion abbrechen soll.
     * 
     * @return true, wenn die Aktion abgebrochen werden soll
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * Returns wheter the progress is indeterminate.
     * 
     * @return true if indeterminate
     */
    public boolean isIndeterminate() {
        return indeterminate;
    }

    /**
     * Sets wheter the progress is indeterminate.
     * 
     * @param intermediate  true if indeterminate. Default: false.
     */
    public void setIndeterminate(boolean intermediate) {
        this.indeterminate = intermediate;
    }
    
}
