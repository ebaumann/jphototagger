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
package de.elmar_baumann.jpt.data;

import com.adobe.xmp.properties.XMPPropertyInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about the current selected file <em>only</em> when
 * <strong>one</strong> file is selected. Used for sharing data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-07
 */
public final class SelectedFile {

    public static final SelectedFile          INSTANCE        = new SelectedFile();
    private             List<XMPPropertyInfo> xmpPropertyInfos;
    private             File                  file            = new File("");

    /**
     * Sets the file and it's metadata.
     *
     * @param file                  file
     * @param xmpPropertyInfos      XMP metadata
     * @throws NullPointerException if the file is null
     */
    public synchronized void setFile(File file, List<XMPPropertyInfo> xmpPropertyInfos) {
        if (file == null) throw new NullPointerException("file == null");

        this.file = file;
        this.xmpPropertyInfos = xmpPropertyInfos == null
                                ? null
                                : new ArrayList<XMPPropertyInfo>(xmpPropertyInfos);
    }

    /**
     *
     * @return File. If not set, a file with an empty name is returned.
     */
    public synchronized File getFile() {
        return file;
    }

    /**
     *
     * @return XMP metadata or null
     */
    public synchronized List<XMPPropertyInfo> getPropertyInfos() {
        return xmpPropertyInfos == null
               ? null
               : new ArrayList<XMPPropertyInfo>(xmpPropertyInfos);
    }

    private SelectedFile() {
    }
}
