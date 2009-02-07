package de.elmar_baumann.lib.persistence;

import java.util.ArrayList;
import java.util.List;


/**
 * Hints for the class {@link PersistentSettings}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     PersistentSettings
 */
public final class PersistentSettingsHints {

    private final List<String> excludedMembers = new ArrayList<String>();
    private boolean setComboBoxContent = false;
    private boolean setListContent = false;
    private boolean tabbedPaneContents = true;
    private String keyPostfix = ""; // NOI18N

    public PersistentSettingsHints() {
    }

    /**
     * Sets a key's postfix. Thus multiple instances of the same class can
     * written persistent if the key is the class name.
     * 
     * @param postfix Postfix. Default: Empty string.
     */
    public void setKeyPostfix(String postfix) {
        this.keyPostfix = postfix;
    }

    /**
     * Returns a key's postfix.
     * 
     * @return Postfix
     * @see #setKeyPostfix(java.lang.String)
     */
    public String getKeyPostfix() {
        return keyPostfix;
    }

    /**
     * Sets that the <strong>content</strong> of
     * {@link javax.swing.JComboBox}es shall be written persistent rather than
     * the index of the selected item.
     * 
     * @param set true, if the content of {@link javax.swing.JComboBox}es shall
     *            be written persistent. Default: false (the index of the
     *            selected item shall be written persistent).
     */
    public void setComboBoxContent(boolean set) {
        setComboBoxContent = set;
    }

    /**
     * Returns wheter the content of {@link javax.swing.JComboBox}es shall be
     * written persistent rather than the index of the selected item.
     * 
     * @return true, if the content of {@link javax.swing.JComboBox}es shall be
     *         written persistent. false, if the index of the selected item
     *         shall be written persistent
     * @see #setComboBoxContent(boolean)
     */
    public boolean isSetComboBoxContent() {
        return setComboBoxContent;
    }

    /**
     * Sets that the <strong>content</strong> of {@link javax.swing.JList}s
     * shall be written persistent rather than the index of the selected value.
     *
     * @param set true, if the content of {@link javax.swing.JList}s shall be
     *            written persistent. Default: false (the index of the selected
     *            value shall be written persistent).
     */
    public void setListContent(boolean set) {
        setListContent = set;
    }

    /**
     * Returns, whether the <strong>content</strong> of {@link javax.swing.JList}s
     * shall be written persistent rather than the index of the selected value.
     * 
     * @return true, if the content of {@link javax.swing.JList}s shall be
     *            written persistent. false if the index of the selected value
     *            shall be written persistent.
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
