/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.database.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL-Joins.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class Join {
    private static final Map<String, String> JOIN_FROM_FILES =
        new HashMap<String, String>();

    static {
        JOIN_FROM_FILES.put("files", "");
        JOIN_FROM_FILES.put(
            "dc_subjects",
            "\\JOIN_TYPE\\ JOIN xmp ON files.id = xmp.id_files"
            + " INNER JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
            + " INNER JOIN dc_subjects"
            + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id");
        JOIN_FROM_FILES.put("exif",
                            "\\JOIN_TYPE\\ JOIN exif"
                            + " on files.id = exif.id_files");
        JOIN_FROM_FILES.put("xmp",
                            "\\JOIN_TYPE\\ JOIN xmp"
                            + " on files.id = xmp.id_files");
    }

    private Join() {}

    /**
     * Type of a SQL join.
     */
    public enum Type {
        INNER("INNER"), LEFT("LEFT");

        private final String string;

        private Type(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static String getJoinToFiles(String tablename, Type type) {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (!JOIN_FROM_FILES.containsKey(tablename)) {
            throw new IllegalArgumentException("Unkown table: " + tablename);
        }

        return JOIN_FROM_FILES.get(tablename).replace("\\JOIN_TYPE\\",
                                   type.string);
    }
}
