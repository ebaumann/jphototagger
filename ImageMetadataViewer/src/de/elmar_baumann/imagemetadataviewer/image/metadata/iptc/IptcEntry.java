package de.elmar_baumann.imagemetadataviewer.image.metadata.iptc;

import com.imagero.reader.iptc.IPTCEntry;
import de.elmar_baumann.imagemetadataviewer.UserSettings;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IPTC-Eintrag in einer Bilddatei. Dekodiert die Daten (getData()) als
 * ISO-8859-1-String.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public class IptcEntry {

    private IPTCEntry entry;

    /**
     * Erzeugt ein neues Objekt.
     * 
     * @param entry IPTC-Eintrag
     */
    public IptcEntry(IPTCEntry entry) {
        this.entry = entry;
    }

    /**
     * Liefert den Namen der IPTC-Eigenschaft.
     * 
     * @return Name
     */
    public String getName() {
        return entry.getEntryMeta().getName();
    }

    /**
     * Liefert die Recordnummer der IPTC-Eigenschaft.
     * 
     * @return Recordnummer
     */
    public int getRecordNumber() {
        return entry.getRecordNumber();
    }

    /**
     * Liefert die Daten der IPTC-Eigenschaft.
     * 
     * @return Daten
     */
    public String getData() {
        return getEncodedData();
    }

    /**
     * Liefert die Datensatznummer der IPTC-Eigenschaft.
     * 
     * @return Datensatznummer.
     */
    public int getDataSetNumber() {
        return entry.getDataSetNumber();
    }

    /**
     * Liefert den Eintrag (zugrunde liegende Daten).
     * 
     * @return Eintrag
     */
    public IPTCEntry getEntry() {
        return entry;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IptcEntry) {
            IptcEntry otherEntry = (IptcEntry) o;
            return getRecordNumber() == otherEntry.getRecordNumber() &&
                getDataSetNumber() == otherEntry.getDataSetNumber() &&
                getData().equals(otherEntry.getData());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.entry != null ? this.entry.hashCode() : 0);
        return hash;
    }

    private String getEncodedData() {
        try {
            return new String(entry.getData(), UserSettings.getInstance().
                getIptcCharset()).trim();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(IptcEntry.class.getName()).
                log(Level.SEVERE, null, ex);
        }
        return ""; // NOI18N
    }
}
