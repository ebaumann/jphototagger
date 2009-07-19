package de.elmar_baumann.imv.image.metadata.iptc;

import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCConstants;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Iptc;
import de.elmar_baumann.imv.resource.Bundle;
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
                        Bundle.getString("IptcMetadata.Info.GetMetadata", // NOI18N
                        imageFile));
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
