/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.iptc;

import com.imagero.reader.iptc.IPTCEntry;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLogger;
import java.util.Arrays;

/**
 * IPTC-Eintrag in einer Bilddatei. Dekodiert die Daten (getData()) als
 * ISO-8859-1-String.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-02-17
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
            return new String(data, UserSettings.INSTANCE.getIptcCharset()).trim();
        } catch (Exception ex) {
            AppLogger.logSevere(IptcEntry.class, ex);
        }
        return "";
    }
}
