package de.elmar_baumann.lib.componentutil;

import javax.swing.ComboBoxModel;

/**
 * Werkzeuge für Comboboxen.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/08/29
 */
public final class ComboBoxUtil {

    /**
     * Liefert, ob ein Combobox-Model ein Item mit bestimmtem String enthält
     * durch Aufruf von <code>equals(string)</code> bei allen Items.
     * 
     * @param comboBox Combobox
     * @param string   Gesuchter String
     * @return         true, wenn die Combobox das Element enthält
     * @see            #getItem(ComboBoxModel, String)
     */
    public static boolean hasItem(ComboBoxModel comboBox, String string) {
        if (comboBox == null)
            throw new NullPointerException("comboBox == null"); // NOI18N
        if (string == null)
            throw new NullPointerException("string == null"); // NOI18N

        for (int index = 0; index < comboBox.getSize(); index++) {
            Object currentItem = comboBox.getElementAt(index);
            if (currentItem.equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert das erste Element eines Combobox-Models, dessen Operation
     * <code>equals(string)</code>  true liefert.
     * 
     * @param comboBoxModel Model
     * @param string        Gesuchter String
     * @return              Element oder null, falls keines gefunden wurde
     * @see                 #hasItem(ComboBoxModel, String)
     */
    public static Object getItem(ComboBoxModel comboBoxModel, String string) {
        if (comboBoxModel == null)
            throw new NullPointerException("comboBoxModel == null"); // NOI18N
        if (string == null)
            throw new NullPointerException("string == null"); // NOI18N

        for (int index = 0; index < comboBoxModel.getSize(); index++) {
            Object currentItem = comboBoxModel.getElementAt(index);
            if (currentItem.equals(string)) {
                return currentItem;
            }
        }
        return null;
    }

    private ComboBoxUtil() {
    }
}
