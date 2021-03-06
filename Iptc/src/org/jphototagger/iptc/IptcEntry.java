package org.jphototagger.iptc;

import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryMeta;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * IPTC-Eintrag in einer Bilddatei. Dekodiert die Daten (getData()) als
 * ISO-8859-1-String.
 *
 * @author Elmar Baumann
 */
public final class IptcEntry {

    private final String name;
    private final byte[] data;
    private final int recordNumber;
    private final int datasetNumber;
    private final IPTCEntryMeta entryMeta;

    /**
     * Erzeugt ein neues Objekt.
     *
     * @param entry IPTC-Eintrag
     */
    public IptcEntry(IPTCEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry == null");
        }

        name = entry.getEntryMeta().getName();
        data = Arrays.copyOf(entry.getData(), entry.getData().length);
        recordNumber = entry.getRecordNumber();
        datasetNumber = entry.getDataSetNumber();
        entryMeta = entry.getEntryMeta();
    }

    /**
     * Liefert den Namen der IPTC-Eigenschaft.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert die Recordnummer der IPTC-Eigenschaft.
     *
     * @return Recordnummer
     */
    public int getRecordNumber() {
        return recordNumber;
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
        return datasetNumber;
    }

    public IPTCEntryMeta getEntryMeta() {
        return entryMeta;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IptcEntry) {
            IptcEntry otherEntry = (IptcEntry) o;

            return (recordNumber == otherEntry.recordNumber) && (datasetNumber == otherEntry.datasetNumber)
                    && getData().equals(otherEntry.getData());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 83 * hash + this.recordNumber;
        hash = 83 * hash + this.datasetNumber;

        return hash;
    }

    private String getEncodedData() {
        try {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            String iptcCharset = prefs.getString(IptcPreferencesKeys.KEY_IPTC_CHARSET);
            if (!StringUtil.hasContent(iptcCharset)) {
                iptcCharset = "ISO-8859-1";
            }

            String encodedData = new String(data, iptcCharset);

            return encodedData.trim();
        } catch (Throwable t) {
            Logger.getLogger(IptcEntry.class.getName()).log(Level.SEVERE, null, t);
        }

        return "";
    }
}
