package de.elmar_baumann.imagemetadataviewer.image.metadata.iptc;

import com.imagero.reader.MetadataUtils;
import com.imagero.reader.iptc.IPTCConstants;
import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryCollection;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imagemetadataviewer.data.Iptc;
import de.elmar_baumann.imagemetadataviewer.event.ErrorEvent;
import de.elmar_baumann.imagemetadataviewer.event.listener.ErrorListeners;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IPTC-Metadaten einer Bilddatei.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/26
 */
public class IptcMetadata {

    /**
     * Liefert die IPTC-Metadaten einer Datei.
     * 
     * @param  filename  Dateiname
     * @return Metadaten oder null bei Lesefehlern
     */
    public Vector<IptcEntry> getMetadata(String filename) {
        if (!FileUtil.existsFile(filename)) {
            return null;
        }
        Vector<IptcEntry> metadata = new Vector<IptcEntry>();

        try {
            IPTCEntryCollection collection = MetadataUtils.getIPTC(new File(filename));
            if (collection != null) {
                addEntries(collection.getEntries(IPTCConstants.RECORD_APPLICATION), metadata);
            }
        } catch (IOException ex) {
            metadata = null;
            Logger.getLogger(IptcMetadata.class.getName()).log(Level.SEVERE, null, ex);
            notifyErrorListener(ex.toString());
        } catch (Exception ex) {
            metadata = null;
            Logger.getLogger(IptcMetadata.class.getName()).log(Level.SEVERE, null, ex);
            notifyErrorListener(ex.toString());
        }
        return metadata;
    }

    private static void addEntries(IPTCEntry[][] entries,
        Vector<IptcEntry> metadata) {
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                addEntries(entries[i], metadata);
            }
        }
    }

    private static void addEntries(IPTCEntry[] entries, Vector<IptcEntry> metadata) {
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

    private void notifyErrorListener(String message) {
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(message, this));
    }

    /**
     * Filtert die Entries.
     * 
     * @param  entries Entries
     * @param  filter  Filter
     * @return Alle Entries mit den im Filter angegebenen Metadaten
     */
    public Vector<IptcEntry> getFilteredEntries(Vector<IptcEntry> entries, IPTCEntryMeta filter) {
        Vector<IptcEntry> filteredEntries = new Vector<IptcEntry>();
        for (IptcEntry entry : entries) {
            if (entry.getEntry().getEntryMeta().equals(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    /**
     * Liefert die IPTC-Daten einer Datei.
     * 
     * @param  filename  Dateiname
     * @return Daten oder null bei Fehlern
     */
    public static Iptc getIptc(String filename) {
        Iptc iptc = null;
        IptcMetadata iptcMetadata = new IptcMetadata();
        Vector<IptcEntry> iptcEntries = iptcMetadata.getMetadata(filename);
        if (iptcEntries != null) {
            iptc = new Iptc();
            for (IptcEntry iptcEntry : iptcEntries) {
                IPTCEntryMeta iptcEntryMeta = iptcEntry.getEntry().getEntryMeta();
                if (iptcEntryMeta.equals(IPTCEntryMeta.BYLINE)) {
                    iptc.addByLineTitle(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.BYLINE_TITLE)) {
                    iptc.addByLineTitle(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.CAPTION_ABSTRACT)) {
                    iptc.setCaptionAbstract(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.CATEGORY)) {
                    iptc.setCategory(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.CITY)) {
                    iptc.setCity(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.CONTENT_LOCATION_CODE)) {
                    iptc.addContentLocationCode(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.CONTENT_LOCATION_NAME)) {
                    iptc.addContentLocationName(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.COPYRIGHT_NOTICE)) {
                    iptc.setCopyrightNotice(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME)) {
                    iptc.setCountryPrimaryLocationName(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.CREDIT)) {
                    iptc.setCredit(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.DATE_CREATED)) {
                    iptc.setCreationDate(getDateFromIptcDateString(iptcEntry.getData()));
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.HEADLINE)) {
                    iptc.setHeadline(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.KEYWORDS)) {
                    iptc.addKeyword(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.OBJECT_NAME)) {
                    iptc.setObjectName(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE)) {
                    iptc.setOriginalTransmissionReference(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.PROVINCE_STATE)) {
                    iptc.setProvinceState(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.SOURCE)) {
                    iptc.setSource(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.SPECIAL_INSTRUCTIONS)) {
                    iptc.setSpecialInstructions(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY)) {
                    iptc.addSupplementalCategory(iptcEntry.getData());
                } else if (iptcEntryMeta.equals(IPTCEntryMeta.WRITER_EDITOR)) {
                    iptc.addWriterEditor(iptcEntry.getData());
                }
            }
        }
        return iptc;
    }

    private static Date getDateFromIptcDateString(String datestring) {
        if (datestring.length() != 8) {
            return null;
        }
        int year = new Integer(datestring.substring(0, 4)).intValue();
        int month = new Integer(datestring.substring(5, 6)).intValue();
        int day = new Integer(datestring.substring(6, 8)).intValue();

        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month - 1, day);

        return new Date(calendar.getTimeInMillis());
    }
}
