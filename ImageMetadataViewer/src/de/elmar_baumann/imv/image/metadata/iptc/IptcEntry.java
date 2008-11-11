package de.elmar_baumann.imv.image.metadata.iptc;

import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.UserSettings;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * IPTC-Eintrag in einer Bilddatei. Dekodiert die Daten (getData()) als
 * ISO-8859-1-String.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public class IptcEntry {

    private String name;
    private byte[] data;
    private int recordNumber;
    private int datasetNumber;
    private IPTCEntryMeta entryMeta;

    /**
     * Erzeugt ein neues Objekt.
     * 
     * @param entry IPTC-Eintrag
     */
    public IptcEntry(IPTCEntry entry) {
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
            return recordNumber == otherEntry.recordNumber &&
                datasetNumber == otherEntry.datasetNumber &&
                getData().equals(otherEntry.getData());
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
            return new String(data, UserSettings.getInstance().
                getIptcCharset()).trim();
        } catch (UnsupportedEncodingException ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
        }
        return ""; // NOI18N
    }
}
