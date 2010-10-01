/*
 * @(#)ListModelWait.java    Created on 2010-10-01
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

import org.jphototagger.program.resource.JptBundle;

import javax.swing.DefaultListModel;

/**
 * Contains exactly one list element with a "wait" text and is a substitute as
 * long as a large list model will be created.
 *
 * @author Elmar Baumann
 */
public final class ListModelWait extends DefaultListModel {
    private static final long         serialVersionUID = 1363478529337093293L;
    public static final ListModelWait INSTANCE         = new ListModelWait();

    public ListModelWait() {
        addElement(JptBundle.INSTANCE.getString("ListModelWait.ItemText"));
    }
}
