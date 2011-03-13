package org.jphototagger.program.image.metadata.iptc;

import com.imagero.reader.iptc.IPTCConstants;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.iptc.IPTCEntryMeta;
import com.imagero.reader.MetadataUtils;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Iptc;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.program.app.AppFileFilters;

/**
 * IPTC metadata of an image file.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class IptcMetadata {

    private static final Logger LOGGER = Logger.getLogger(IptcMetadata.class.getName());

   /**
     * Returns {@link IptcEntry} instances of an image file.
     *
     * @param  imageFile image file or null
     * @return           Metadata or empty list if the image has no IPTC
     *                   metadata or when errors occur
     */
    public static List<IptcEntry> getIptcEntries(File imageFile) {
        List<IptcEntry> metadata = new ArrayList<IptcEntry>();

        if ((imageFile != null) && imageFile.exists() && !AppFileFilters.INSTANCE.isUserDefinedFileType(imageFile)) {
            try {
                AppLogger.logInfo(IptcMetadata.class, "IptcMetadata.Info.GetMetadata", imageFile);
                LOGGER.log(Level.INFO, "Reading IPTC from image file ''{0}'', size {1} Bytes", new Object[]{imageFile, imageFile.length()});

                IPTCEntryCollection collection = MetadataUtils.getIPTC(imageFile);

                if (collection != null) {
                    addEntries(collection.getEntries(IPTCConstants.RECORD_APPLICATION), metadata);
                }
            } catch (Exception ex) {
                AppLogger.logSevere(IptcMetadata.class, ex);
            }
        }

        return metadata;
    }

    private static void addEntries(IPTCEntry[][] entries, List<IptcEntry> metadata) {
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

                if ((currentEntry != null) &&!isVersionInfo(currentEntry)) {
                    IptcEntry newEntry = new IptcEntry(currentEntry);

                    if (hasContent(newEntry) &&!metadata.contains(newEntry)) {
                        metadata.add(newEntry);
                    }
                }
            }
        }
    }

    private static boolean hasContent(IptcEntry entry) {
        return (entry.getData() != null) &&!entry.getData().trim().isEmpty();
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

    private IptcMetadata() {}
}
