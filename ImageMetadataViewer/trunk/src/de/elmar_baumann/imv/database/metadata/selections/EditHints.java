package de.elmar_baumann.imv.database.metadata.selections;

/**
 * Hinweise für Spalten, die bearbeitet werden können.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public final class EditHints {

    private final boolean repeatable;
    private final SizeEditField sizeEditField;

    /**
     * Vorschlag: Größe des Editierfelds.
     */
    public enum SizeEditField {

        /**
         * Kleines Feld reicht aus (einzeilig)
         */
        SMALL,
        /**
         * "Mittleres" Feld reicht aus (etwa drei Zeilen)
         */
        MEDIUM,
        /**
         * Größeres Feld
         */
        LARGE
    }

    /**
     * Konstruktor.
     * 
     * @param repeatable     true, wenn der Spaltenwert sich wiederholt
     * @param sizeEditField  Größenvorschlag
     */
    public EditHints(boolean repeatable, SizeEditField sizeEditField) {
        this.repeatable = repeatable;
        this.sizeEditField = sizeEditField;
    }

    /**
     * Liefert, ob der Spaltenwert sich wiederholt.
     * 
     * @return true, wenn der Spaltenwert sich wiederholt
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * Liefert den Größenvorschlag.
     * 
     * @return Größenvorschlag
     */
    public SizeEditField getSizeEditField() {
        return sizeEditField;
    }
}
