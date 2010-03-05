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

import de.elmar_baumann.jpt.app.AppLogger;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-11-02
 */
public final class ColumnUtil {

    /**
     * Creates via Reflection columns from the keys of the columns
     * ({@link Column#getKey()}).
     *
     * @param  columnKeys  column keys
     * @return columns of valid keys
     */
    public static List<Column> columnKeysToColumns(List<String> columnKeys) {
        List<Column> columns = new ArrayList<Column>();

        for (String key : columnKeys) {
            try {
                if (!key.contains("ColumnXmpPhotoshopCategory")
                        &&!key.contains(
                            "ColumnXmpPhotoshopSupplementalcategories")) {
                    Class<?>                             cl    =
                        Class.forName(key);
                    @SuppressWarnings("unchecked") Field field =
                        cl.getField("INSTANCE");

                    if (field.get(null) instanceof Column) {
                        columns.add((Column) field.get(null));
                    }
                }
            } catch (Exception ex) {
                AppLogger.logSevere(ColumnUtil.class, ex);
            }
        }

        return columns;
    }

    /**
     * Returns the descriptions of columns ({@link Column#getDescription()}).
     *
     * @param  columns  columns
     * @return descriptions in the same order as in <code>columns</code>
     */
    public static List<String> getDescriptionsOfColumns(List<Column> columns) {
        List<String> text = new ArrayList<String>();

        for (Column column : columns) {
            text.add(column.getDescription());
        }

        return text;
    }

    private ColumnUtil() {}
}
