package de.elmar_baumann.imv.image.metadata.iptc;

import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCConstants;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.data.Iptc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * IPTC-Metadaten einer Bilddatei.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IptcMetadata {

    /**
     * Liefert die IPTC-Metadaten einer Datei.
     * 
     * @param  file  Datei
     * @return Metadaten oder null bei Lesefehlern
     */
    public static List<IptcEntry> getMetadata(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        List<IptcEntry> metadata = new ArrayList<IptcEntry>();

        try {
            IPTCEntryCollection collection = MetadataUtils.getIPTC(file);
            if (collection != null) {
                addEntries(collection.getEntries(IPTCConstants.RECORD_APPLICATION), metadata);
            }
        } catch (IOException ex) {
            metadata = null;
            de.elmar_baumann.imv.Log.logWarning(IptcMetadata.class, ex);
        } catch (Exception ex) {
            metadata = null;
            de.elmar_baumann.imv.Log.logWarning(IptcMetadata.class, ex);
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
     * Filtert die Entries.
     * 
     * @param  entries Entries
     * @param  filter  Filter
     * @return Alle Entries mit den im Filter angegebenen Metadaten
     */
    public static List<IptcEntry> getFilteredEntries(List<IptcEntry> entries, IPTCEntryMeta filter) {
        List<IptcEntry> filteredEntries = new ArrayList<IptcEntry>();
        for (IptcEntry entry : entries) {
            if (entry.getEntryMeta().equals(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    /**
     * Liefert die IPTC-Daten einer Datei.
     * 
     * @param  file  Datei
     * @return Daten oder null bei Fehlern
     */
    public static Iptc getIptc(File file) {
        Iptc iptc = null;
        List<IptcEntry> iptcEntries = getMetadata(file);
        if (iptcEntries != null) {
            iptc = new Iptc();
            for (IptcEntry iptcEntry : iptcEntries) {
                IPTCEntryMeta iptcEntryMeta = iptcEntry.getEntryMeta();
                iptc.setValue(iptcEntryMeta, iptcEntry.getData());
            }
        }
        return iptc;
    }

    private IptcMetadata() {}
}
