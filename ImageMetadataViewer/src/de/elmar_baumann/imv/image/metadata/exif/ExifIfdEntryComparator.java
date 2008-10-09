package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.tiff.IFDEntry;
import java.util.Comparator;

/**
 * Vergleicht Objekte des Typs IFDEntry.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/24
 * @see     com.imagero.reader.tiff.IFDEntry
 */
public class ExifIfdEntryComparator implements Comparator<IFDEntry> {

    @Override
    public int compare(IFDEntry o1, IFDEntry o2) {
        return o1.compareTo(o2);
    }
}
