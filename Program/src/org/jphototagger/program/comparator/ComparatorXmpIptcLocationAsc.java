/*
 * @(#)ComparatorXmpIptcLocationAsc.java    Created on 2009-12-16
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

package org.jphototagger.program.comparator;

import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.text.Collator;

import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorXmpIptcLocationAsc extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long  serialVersionUID = -6946394073635783198L;
    private transient Collator collator         = Collator.getInstance();

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp    xmpLeft  =
            DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileLeft);
        Xmp    xmpRight =
            DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileRight);
        Object locLeft  = (xmpLeft == null)
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
                 ? -1
                 : ((locLeft != null) && (locRight == null))
                   ? 1
                   : collator.compare(locLeft, locRight);
    }
}
