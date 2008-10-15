package de.elmar_baumann.imv.image.metadata.exif;

import java.util.Comparator;

/**
 * Vergleicht Objekte des Typs IdfEntryProxy.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/24
 * @see     de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy
 */
public class ExifIfdEntryComparator implements Comparator<IdfEntryProxy> {

    @Override
    public int compare(IdfEntryProxy o1, IdfEntryProxy o2) {
        return o1.compareTo(o2);
    }
}
