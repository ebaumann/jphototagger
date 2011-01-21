package org.jphototagger.program.event;

/**
 * Fortschritts-Ereignis.
 *
 * @author Elmar Baumann
 */
public final class ProgressEvent {
    private final Object source;
    private int          maximum;
    private int          minimum;
    private boolean      indeterminate;
    private int          value;
    private long         milliSecondsRemaining;
    private Object       info;
    private boolean      cancel;

    /**
     * Konstruktor für Ereignisse mit bekanntem Umfang (Minimum, Maximum und
     * aktueller Wert sind bekannt).
     *
     * <ul>
     * <li>{@link #isIndeterminate()} liefert <code>false</code>
     * <li>{@link #getMilliSecondsRemaining()} liefert <code>-1</code>
     * </ul>
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
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        this.source  = source;
        this.minimum = minimum;
        this.maximum = maximum;
        this.value   = value;
        this.info    = info;
    }

    /**
     * Konstruktor für Ereignisse mit bekanntem Umfang (Minimum, Maximum und
     * aktueller Wert sind bekannt) sowie bekannter Dauer.
     *
     * {@link #isIndeterminate()} liefert <code>false</code>.
     *
     * @param source                 Quelle des Ereignisses
     * @param minimum                Minimale Ereignisanzahl
     * @param maximum                Maximale Ereignisanzahl
     * @param value                  Aktuelles Ereignis (Wert zwischen minimaler
     *                               und maximaler Ereignisanzahl)
     * @param milliSecondsRemaining  Verbleibende Zeit in Millisekunden
     * @param info                   Beliebige Information
     */
    public ProgressEvent(Object source, int minimum, int maximum, int value,
                         long milliSecondsRemaining, Object info) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        this.source                = source;
        this.minimum               = minimum;
        this.maximum               = maximum;
        this.value                 = value;
        this.milliSecondsRemaining = milliSecondsRemaining;
        this.info                  = info;
    }

    /**
     * Konstruktor für Ereignisse mit unbekanntem Umfang.
     *
     * <ul>
     * <li>{@link #isIndeterminate()} liefert <code>true</code>
     * <li>{@link #getMilliSecondsRemaining()} liefert <code>-1</code>
     * </ul>
     *
     * @param source  Quelle des Ereignisses
     * @param info    Beliebige Information
     */
    public ProgressEvent(Object source, Object info) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        this.source   = source;
        this.info     = info;
        indeterminate = true;
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
     * Liefert die minimale Ereignisanzahl.
     *
     * @return Minimale Ereignisanzahl
     */
    public int getMinimum() {
        return minimum;
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
     * Teilt der Quelle mit, dass die Aktion abgebrochen werden soll.
     */
    public void cancel() {
        cancel = true;
    }

    /**
     * Liefert, ob die Quelle die Aktion abbrechen soll.
     *
     * @return true, wenn die Aktion abgebrochen werden soll
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * Returns wheter the progress is indeterminate.
     *
     * @return true if indeterminate
     */
    public boolean isIndeterminate() {
        return indeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public void setMilliSecondsRemaining(long milliSecondsRemaining) {
        this.milliSecondsRemaining = milliSecondsRemaining;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
