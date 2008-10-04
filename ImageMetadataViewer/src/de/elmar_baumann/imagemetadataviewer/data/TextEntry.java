package de.elmar_baumann.imagemetadataviewer.data;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.lib.template.Pair;

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
     * Liefert, ob der Eintrag leer ist (keinen Inhalt hat).
     * 
     * @return true, wenn leer
     */
    public boolean isEmpty();
}
