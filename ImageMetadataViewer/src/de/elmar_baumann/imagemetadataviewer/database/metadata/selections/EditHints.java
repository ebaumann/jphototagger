package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

/**
 * Hinweise für Spalten, die bearbeitet werden können.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class EditHints {

    /**
     * Vorschlag: Größe des Editierfelds.
     */
    public enum SizeEditField {

        /**
         * Kleines Feld reicht aus (einzeilig)
         */
        small,
        /**
         * "Mittleres" Feld reicht aus (etwa drei Zeilen)
         */
        medium,
        /**
         * Größeres Feld
         */
        large
    }
    private boolean repeatable;
    private SizeEditField sizeEditField;

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
     * Setzt, ob der Spaltenwert sich wiederholt.
     * 
     * @param repeatable true, wenn der Spaltenwert sich wiederholt
     */
    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    /**
     * Liefert den Größenvorschlag.
     * 
     * @return Größenvorschlag
     */
    public SizeEditField getSizeEditField() {
        return sizeEditField;
    }

    /**
     * Setzt den Größenvorschlag.
     * 
     * @param sizeEditField Größenvorschlag
     */
    public void setSizeEditField(SizeEditField sizeEditField) {
        this.sizeEditField = sizeEditField;
    }
}
