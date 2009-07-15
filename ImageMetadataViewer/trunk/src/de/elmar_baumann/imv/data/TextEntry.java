package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Ein Texteintrag und die IPTC- und XMP-Spalten, für die dieser gilt.
 * 
 * Zurzeit ist die XMP-Spalte nicht notwendig, könnte zukünftig aber
 * benutzt werden.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public interface TextEntry {

    /**
     * Liefert den Text.
     * 
     * @return Text
     */
    public String getText();

    /**
     * Setzt den Text.
     * 
     * @param text Text
     */
    public void setText(String text);

    /**
     * Liefert die XMP-Tabellenspalte, die den Text enthält.
     * 
     * @return Tabellenspalte
     */
    public Column getColumn();

    /**
     * Setzt den Fokus auf das zugehörige Eingabefeld.
     */
    public void focus();

    /**
     * Setzt, ob Bearbeiten möglich ist.
     * 
     * @param editable true, wenn möglich
     */
    public void setEditable(boolean editable);

    /**
     * Returns whether editing is possible.
     *
     * @return true if editing is possible.
     */
    public boolean isEditable();

    /**
     * Liefert, ob der Eintrag leer ist (keinen Inhalt hat).
     * 
     * @return true, wenn leer
     */
    public boolean isEmpty();
    
    /**
     * Enables autocomplete.
     */
    public void setAutocomplete();
    
    /**
     * Returns whether the text has been changed since the last call
     * to {@link #setText(java.lang.String)}.
     * 
     * @return true if changed
     */
    public boolean isDirty();
    
    /**
     * Sets how the entry shall behave like changes since last call to
     * {@link #setText(java.lang.String)}.
     * 
     * @param dirty  true if the text was changed
     */
    public void setDirty(boolean dirty);
    
    public TextEntry clone();
}
