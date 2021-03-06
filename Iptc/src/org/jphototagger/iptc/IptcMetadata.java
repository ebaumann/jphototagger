package org.jphototagger.iptc;

import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCConstants;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.iptc.IPTCEntryMeta;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.metadata.iptc.Iptc;

/**
 * IPTC metadata of an image file.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class IptcMetadata {

    private static final Logger LOGGER = Logger.getLogger(IptcMetadata.class.getName());

    /**
     * Returns {@code IptcEntry} instances of an image file.
     *
     * @param  imageFile image file or null
     * @return           Metadata or empty list if the image has no IPTC
     *                   metadata or when errors occur
     */
    public static List<IptcEntry> getIptcEntries(File imageFile) {
        List<IptcEntry> metadata = new ArrayList<>();

        if ((imageFile != null) && imageFile.exists() && IptcSupport.INSTANCE.canReadIptc(imageFile)) {
            try {
                LOGGER.log(Level.INFO, "Reading IPTC from image file ''{0}'', size {1} Bytes", new Object[]{imageFile, imageFile.length()});

                IPTCEntryCollection collection = MetadataUtils.getIPTC(imageFile);

                if (collection != null) {
                    addEntries(collection.getEntries(IPTCConstants.RECORD_APPLICATION), metadata);
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, null, t);
            }
        }

        return metadata;
    }

    private static void addEntries(IPTCEntry[][] entries, List<IptcEntry> metadata) {
        if (entries != null) {
            for (IPTCEntry[] entrie : entries) {
                addEntries(entrie, metadata);
            }
        }
    }

    private static void addEntries(IPTCEntry[] entries, List<IptcEntry> metadata) {
        if (entries != null) {
            for (IPTCEntry currentEntry : entries) {
                if ((currentEntry != null) && !isVersionInfo(currentEntry)) {
                    IptcEntry newEntry = new IptcEntry(currentEntry);

                    if (hasContent(newEntry) && !metadata.contains(newEntry)) {
                        metadata.add(newEntry);
                    }
                }
            }
        }
    }

    private static boolean hasContent(IptcEntry entry) {
        return (entry.getData() != null) && !entry.getData().trim().isEmpty();
    }

    private static boolean isVersionInfo(IPTCEntry entry) {
        return (entry.getRecordNumber() == 2) && (entry.getDataSetNumber() == 0);
    }

    /**
     * Filters IPTC entries.
     *
     * @param  entries IPTC entries
     * @param  filter  filter
     * @return         filtered entries
     */
    public static List<IptcEntry> getFilteredEntries(List<IptcEntry> entries, IPTCEntryMeta filter) {
        if (entries == null) {
            throw new NullPointerException("entries == null");
        }

        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        List<IptcEntry> filteredEntries = new ArrayList<>();

        for (IptcEntry entry : entries) {
            if (entry.getEntryMeta().equals(filter)) {
                filteredEntries.add(entry);
            }
        }

        return filteredEntries;
    }

    /**
     * Returns a {@code Iptc} instance of an image file.
     *
     * @param  imageFile image file or null
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

    public static IPTCEntry findEntry(Collection<? extends IPTCEntry> entries, int recordNumber, int dataSetNumber) {
        if (entries == null) {
            throw new NullPointerException("entries == null");
        }

        for (IPTCEntry entry : entries) {
            int recordNo = entry.getRecordNumber();
            int dataSetNo = entry.getDataSetNumber();

            if (recordNo == recordNumber && dataSetNo == dataSetNumber) {
                return entry;
            }
        }

        return null;
    }

    private IptcMetadata() {
    }
}
