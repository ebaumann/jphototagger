package de.elmar_baumann.imv.image.metadata.exif;

import java.util.Comparator;

/**
 * Vergleicht Objekte des Typs IdfEntryProxy.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-24
 * @see     de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy
 */
public final class ExifIfdEntryComparator implements Comparator<IdfEntryProxy> {

    public static final ExifIfdEntryComparator INSTANCE = new ExifIfdEntryComparator();

    @Override
    public int compare(IdfEntryProxy o1, IdfEntryProxy o2) {
        return o1.compareTo(o2);
    }

    private ExifIfdEntryComparator() {}
}
