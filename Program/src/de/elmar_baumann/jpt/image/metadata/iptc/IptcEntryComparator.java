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

import java.util.Comparator;

/**
 * Vergleicht IptcEntry-Objekte. Vergleichskriterien:
 * <ol>
 * <li>Recordnummer</li>
 * <li>Datensatznummer</li>
 * <li>Daten</li>
 * </ol>
 * Der Name wird nicht verglichen, denn Recordnummer und Datensatznummer
 * identifizieren diesen eindeutig; er ist redundand.
 *
 * @author  Elmar Baumann
 * @version 2008-02-17
 * @see     IptcEntry
 */
public final class IptcEntryComparator implements Comparator<IptcEntry> {

    public static final IptcEntryComparator INSTANCE = new IptcEntryComparator();

    @Override
    public int compare(IptcEntry o1, IptcEntry o2) {
        if (o1.getRecordNumber() < o2.getRecordNumber()) {
            return -1;
        }
        if (o1.getRecordNumber() > o2.getRecordNumber()) {
            return +1;
        }
        if (o1.getRecordNumber() == o2.getRecordNumber()) {
            if (o1.getDataSetNumber() < o2.getDataSetNumber()) {
                return -1;
            }
            if (o1.getDataSetNumber() > o2.getDataSetNumber()) {
                return +1;
            }
        }
        return o1.getData().compareTo(o2.getData());
    }

    private IptcEntryComparator() {}
}
