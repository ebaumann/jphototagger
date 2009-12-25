/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
 * @version 2007-08-29
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
            throw new NullPointerException("comboBox == null");
        if (string == null)
            throw new NullPointerException("string == null");

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
            throw new NullPointerException("comboBoxModel == null");
        if (string == null)
            throw new NullPointerException("string == null");

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
