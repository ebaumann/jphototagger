/*
 * @(#)ComparatorXmpIptcLocationDesc.java    Created on 2009-12-16
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

package de.elmar_baumann.jpt.comparator;

import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.text.Collator;

import java.util.Comparator;

/**
 *
 * @author  Elmar Baumann
 */
public final class ComparatorXmpIptcLocationDesc extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long  serialVersionUID = -3947931666327801561L;
    private transient Collator collator         = Collator.getInstance();

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp xmpLeft =
            DatabaseImageFiles.INSTANCE.getXmpOf(fileLeft.getAbsolutePath());
        Xmp xmpRight =
            DatabaseImageFiles.INSTANCE.getXmpOf(fileRight.getAbsolutePath());
        Object locLeft = (xmpLeft == null)
                         ? null
                         : xmpLeft.getValue(
                             ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        Object locRight = (xmpRight == null)
                          ? null
                          : xmpRight.getValue(
                              ColumnXmpIptc4xmpcoreLocation.INSTANCE);

        return ((locLeft == null) && (locRight == null))
               ? 0
               : ((locLeft == null) && (locRight != null))
                 ? 1
                 : ((locLeft != null) && (locRight == null))
                   ? -1
                   : collator.compare(locRight, locLeft);
    }
}
