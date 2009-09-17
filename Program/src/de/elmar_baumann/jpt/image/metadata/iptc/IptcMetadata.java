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
package de.elmar_baumann.jpt.image.metadata.iptc;

import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCConstants;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Iptc;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * IPTC metadata of an image file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IptcMetadata {

    /**
     * Returns {@link IptcEntry} instances of an image file.
     * 
     * @param  imageFile image file
     * @return           Metadata or empty list if the image has no IPTC
     *                   metadata or when errors occur
     */
    public static List<IptcEntry> getIptcEntries(File imageFile) {
        List<IptcEntry> metadata = new ArrayList<IptcEntry>();
        if (imageFile != null && imageFile.exists()) {
            try {
                AppLog.logInfo(IptcMetadata.class,
                        "IptcMetadata.Info.GetMetadata", imageFile); // NOI18N
                IPTCEntryCollection collection =
                        MetadataUtils.getIPTC(imageFile);
                if (collection != null) {
                    addEntries(collection.getEntries(
                            IPTCConstants.RECORD_APPLICATION), metadata);
                }
            } catch (Exception ex) {
                AppLog.logSevere(IptcMetadata.class, ex);
            }
        }
        return metadata;
    }

    private static void addEntries(IPTCEntry[][] entries,
            List<IptcEntry> metadata) {
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                addEntries(entries[i], metadata);
            }
        }
    }

    private static void addEntries(IPTCEntry[] entries, List<IptcEntry> metadata) {
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                IPTCEntry currentEntry = entries[i];
                if (currentEntry != null && !isVersionInfo(currentEntry)) {
                    IptcEntry newEntry = new IptcEntry(currentEntry);
                    if (hasContent(newEntry) && !metadata.contains(newEntry)) {
                        metadata.add(newEntry);
                    }
                }
            }
        }
    }

    private static boolean hasContent(IptcEntry entry) {
        return entry.getData() != null && !entry.getData().trim().isEmpty();
    }

    private static boolean isVersionInfo(IPTCEntry entry) {
        return entry.getRecordNumber() == 2 && entry.getDataSetNumber() == 0;
    }

    /**
     * Filters IPTC entries.
     * 
     * @param  entries IPTC entries
     * @param  filter  filter
     * @return         filtered entries
     */
    public static List<IptcEntry> getFilteredEntries(
            List<IptcEntry> entries, IPTCEntryMeta filter) {
        List<IptcEntry> filteredEntries = new ArrayList<IptcEntry>();
        for (IptcEntry entry : entries) {
            if (entry.getEntryMeta().equals(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    /**
     * Returns a {@link Iptc} instance of an image file.
     * 
     * @param  imageFile image file
     * @return           IPTC of that image file or null if the image has no
     *                   IPTC metadata or when errors occur
     */
    public static Iptc getIptc(File imageFile) {
        Iptc iptc = null;
        List<IptcEntry> iptcEntries = getIptcEntries(imageFile);
        if (iptcEntries.size() > 0) {
            iptc = new Iptc();
            for (IptcEntry iptcEntry : iptcEntries) {
                IPTCEntryMeta iptcEntryMeta = iptcEntry.getEntryMeta();
                iptc.setValue(iptcEntryMeta, iptcEntry.getData());
            }
        }
        return iptc;
    }

    private IptcMetadata() {
    }
}
