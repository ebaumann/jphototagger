package de.elmar_baumann.imv;

import java.awt.Color;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/19
 */
public final class AppColors {

    /**
     * Vordergrundfarbe für Tabellentext, der in der Datenbank gespeichert ist.
     */
    public static final Color colorForegroundTableTextStoredInDatabase = Color.BLACK;
    /**
     * Hintergrundfarbe für Tabellentext, der in der Datenbank gespeichert ist.
     */
    public static final Color colorBackgroundTableTextStoredInDatabase = new Color(251, 249, 241);
    /**
     * Vordergrundfarbe für selektierten Tabellentext.
     */
    public static final Color colorForegroundTableTextSelected = Color.BLACK;
    /**
     * Vordergrundfarbe für selektierten Tabellentext.
     */
    public static final Color colorBackgroundTableTextSelected = new Color(226, 226, 255);
    /**
     * Standard-Vordergrundfarbe für Text in Tabellen.
     */
    public static final Color colorForegroundTableTextDefault = Color.BLACK;
    /**
     * Standard-Hintergrundfarbe für Text in Tabellen.
     */
    public static final Color colorBackgroundTableTextDefault = Color.WHITE;

    private AppColors() {
    }
}
