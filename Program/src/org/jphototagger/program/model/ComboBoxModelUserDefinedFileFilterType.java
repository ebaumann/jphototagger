/*
 * @(#)ListModelUserDefinedFileFilter.java    Created on 2010-03-30
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import javax.swing.DefaultComboBoxModel;
import org.jphototagger.program.data.UserDefinedFileFilter;


/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelUserDefinedFileFilterType extends DefaultComboBoxModel
         {
    private static final long serialVersionUID = 6723254193291648654L;

    public ComboBoxModelUserDefinedFileFilterType() {
        addElements();
    }

    private void addElements() {
        for (UserDefinedFileFilter.Type type : UserDefinedFileFilter.Type.values()) {
            addElement(type);
        }
    }
}
