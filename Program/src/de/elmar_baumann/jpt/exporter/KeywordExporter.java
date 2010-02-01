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
package de.elmar_baumann.jpt.exporter;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 * Exports keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public interface KeywordExporter {

    public void export(File file);

    /**
     * Returns the filter of files that can be exported.
     *
     * @return file filter
     */
    public FileFilter getFileFilter();

    /**
     * Returns a description of this exporter.
     *
     * @return description, e.g.
     *         <code>"Adobe Photoshop Lightroom keywords"</code>
     */
    public String getDescription();

    /**
     * Returns an icon representation of this exporter.
     *
     * @return icon or null
     */
    public Icon getIcon();
}
