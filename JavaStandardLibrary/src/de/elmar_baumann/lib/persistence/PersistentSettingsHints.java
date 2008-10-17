package de.elmar_baumann.lib.persistence;

import java.util.ArrayList;
import java.util.List;


/**
 * Hinweise für die Klasse {@link PersistentSettings}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     de.elmar_baumann.lib.persistence.PersistentSettings
 */
public class PersistentSettingsHints {

    private List<String> excludedMembers = new ArrayList<String>();
    private boolean setComboBoxContent = false;
    private boolean setListContent = false;
    private boolean tabbedPaneContents = true;
    private String keyPostfix = ""; // NOI18N

    public PersistentSettingsHints() {
    }

    /**
     * Setzt ein Postfix für den Schlüssel. So können beispielsweise mehrere
     * Instanzen des gleichen Objekts persistent gespeichert werden.
     * 
     * @param postfix Postfix. Default: Leerstring.
     */
    public void setKeyPostfix(String postfix) {
        this.keyPostfix = postfix;
    }

    /**
     * Liefert das Postfix für den Schlüssel.
     * 
     * @return Postfix
     */
    public String getKeyPostfix() {
        return keyPostfix;
    }

    /**
     * Setzt, dass der Inhalt von Comboboxen persistent gespeichert wird.
     * Andernfalls wird nur der Index des selektierten Items gespeichert.
     * 
     * @param set true, wenn der Inhalt persistent gespeichert werden soll.
     *            Default: false.
     */
    public void setComboBoxContent(boolean set) {
        setComboBoxContent = set;
    }

    /**
     * Liefert, ob der Inhalt von Comboboxen persistent gespeichert werden
     * soll.
     * 
     * @return true, wenn der Inhalt von Comboboxen persistent gespeichert
     *         werden soll
     */
    public boolean isSetComboBoxContent() {
        return setComboBoxContent;
    }

    /**
     * Setzt, dass der Inhalt von Listen persistent gespeichert wird.
     * Andernfalls wird nur der Index des selektierten Werts gespeichert.
     * 
     * @param set true, wenn der Inhalt persistent gespeichert werden soll.
     *            Default: false.
     */
    public void setListContent(boolean set) {
        setListContent = set;
    }

    /**
     * Liefert, ob der Inhalt von Listen persistent gespeichert werden
     * soll.
     * 
     * @return true, wenn der Inhalt von Listen persistent gespeichert
     *         werden soll
     */
    public boolean isSetListContent() {
        return setListContent;
    }

    /**
     * Returns wheter the tabbed pane contents should be set.
     * 
     * @return true if set
     */
    public boolean isTabbedPaneContents() {
        return tabbedPaneContents;
    }

    /**
     * Sets whether the tabbed pane conents should be set.
     * 
     * @param setTabbedPaneContents  true, if set. Default: true
     */
    public void setTabbedPaneContents(boolean setTabbedPaneContents) {
        this.tabbedPaneContents = setTabbedPaneContents;
    }

    /**
     * Fügt ein Attribut hinzu, dessen Inhalt <em>nicht</em> persistent gespeichert
     * werden soll.
     * 
     * @param member Attribut, genauer Pfad, z.B.
     *               <code>de.elmar_baumann.imv.view.AppPanel.tableXmp</code>
     *               Default: Kein Attribut ist ausgeschlossen.
     */
    public void addExcludedMember(String member) {
        excludedMembers.add(member);
    }

    /**
     * Liefert, ob der Inhalt eines Attributs nicht gespeichert werden soll.
     * 
     * @param member Attribut, genauer Pfad, z.B.
     *               <code>de.elmar_baumann.imv.view.AppPanel.tableXmp</code>
     * @return       true wenn der Inhalt des Attributs nicht gespeichert werden soll
     */
    public boolean isExcludedMember(String member) {
        return excludedMembers.contains(member);
    }

    /**
     * Liefert, ob der Inhalt eines Attributs gespeichert werden soll. Diese
     * Operation ist invers zu
     * {@link PersistentSettingsHints#isExcludedMember(java.lang.String)}.
     * 
     * @param member Attribut, genauer Pfad, z.B.
     *               <code>de.elmar_baumann.imv.view.AppPanel.tableXmp</code>
     * @return       true, wenn der Inhalt des Attributs gespeichert werden soll
     */
    public boolean isPersistent(String member) {
        return !isExcludedMember(member);
    }
}
