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

package de.elmar_baumann.jpt.database.metadata.file;

import de.elmar_baumann.jpt.database.metadata.Table;

/**
 * Tabelle <code>files</code>.
 *
 * @author  Elmar Baumann
 * @version 2008-08-27
 */
public final class TableFiles extends Table {
    public static final TableFiles INSTANCE = new TableFiles();

    private TableFiles() {
        super("files");
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnFilesId.INSTANCE);
        addColumn(ColumnFilesFilename.INSTANCE);
        addColumn(ColumnFilesLastModified.INSTANCE);
        addColumn(ColumnFilesThumbnail.INSTANCE);
    }
}
