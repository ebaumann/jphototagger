/*
 * @(#)ComboBoxUtil.java    Created on 2007-08-29
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.componentutil;

import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;

/**
 * Werkzeuge für Comboboxen.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
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
        if (comboBox == null) {
            throw new NullPointerException("comboBox == null");
        }

        if (string == null) {
            throw new NullPointerException("string == null");
        }

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
        if (comboBoxModel == null) {
            throw new NullPointerException("comboBoxModel == null");
        }

        if (string == null) {
            throw new NullPointerException("string == null");
        }

        for (int index = 0; index < comboBoxModel.getSize(); index++) {
            Object currentItem = comboBoxModel.getElementAt(index);

            if (currentItem.equals(string)) {
                return currentItem;
            }
        }

        return null;
    }

    /**
     * Selects in a combo box model the first item matching a string.
     * <p>
     * The <code>toString()</code> method of the combo box items will be
     * compared against the string.
     *
     * @param model  model
     * @param string string to select
     */
    public static void selectString(final ComboBoxModel model,
                                    final String string) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        if (string == null) {
            throw new NullPointerException("string == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int    size    = model.getSize();
                Object selItem = model.getSelectedItem();

                for (int i = 0; i < size; i++) {
                    Object element = model.getElementAt(i);

                    if ((element instanceof String)
                            && ((String) element).equals(string)) {
                        if (element != selItem) {
                            model.setSelectedItem(element);
                        }

                        return;
                    }
                }
            }
        });
    }

    private ComboBoxUtil() {}
}
