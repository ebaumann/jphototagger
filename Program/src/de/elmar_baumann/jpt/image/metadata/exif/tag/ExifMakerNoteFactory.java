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
package de.elmar_baumann.jpt.image.metadata.exif.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-30
 */
public final class ExifMakerNoteFactory {

    public static final  ExifMakerNoteFactory      INSTANCE             = new ExifMakerNoteFactory();
    private static final String                    PROPERTY_FILE_PREFIX = "de/elmar_baumann/jpt/resource/properties/ExifMakerNote_";
    private static final Collection<ExifMakerNote> MAKER_NOTES          = new ArrayList<ExifMakerNote>();

    static {
        
        boolean exists = true;
        int     index  = 0;
        while (exists) {

            try {
                // FIXME: Checking for existance rather than using the exception
                // as stop criteria or even better: Reading the package content
                // and getting all names of property files starting with
                // "ExifMakerNote_" and load them
                ResourceBundle bundle = ResourceBundle.getBundle(
                            PROPERTY_FILE_PREFIX + Integer.toString(index++));
                MAKER_NOTES.add(new ExifMakerNote(bundle));
            } catch (Exception ex) {
                exists = false;
            }
        }
    }

    public ExifMakerNote get(byte[] rawValue) {
        for (ExifMakerNote makerNote : MAKER_NOTES) {
            if (makerNote.matches(rawValue)) return makerNote;
        }
        return null;
    }

    private ExifMakerNoteFactory() {
    }

}
