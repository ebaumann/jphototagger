/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.comparator;

import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.lib.util.ClassNameEquality;
import java.io.File;
import java.util.Comparator;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-16
 */
public final class ComparatorXmpRatingDesc
        extends    ClassNameEquality
        implements Comparator<File> {

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp  xmpLeft     = DatabaseImageFiles.INSTANCE.getXmpOfFile(fileLeft.getAbsolutePath());
        Xmp  xmpRight    = DatabaseImageFiles.INSTANCE.getXmpOfFile(fileRight.getAbsolutePath());
        Long ratingLeft  = xmpLeft .getRating();
        Long ratingRight = xmpRight.getRating();

        return ratingLeft == null && ratingRight == null
                ? 0
                : ratingLeft == null && ratingRight != null
                ? 1
                : ratingLeft != null && ratingRight == null
                ? -1
                : (int) (ratingRight - ratingLeft)
                ;
    }
}
