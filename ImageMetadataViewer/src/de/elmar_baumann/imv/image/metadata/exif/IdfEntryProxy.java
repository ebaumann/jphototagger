package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.tiff.IFDEntry;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy for {@link com.imagero.reader.tiff.IFDEntry}. Reason: Files are
 * locked if used and could not be deleted and renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/15
 */
public class IdfEntryProxy implements Comparable<IdfEntryProxy> {

    private int tag;
    private byte[] rawValue;
    private String string;
    private String name;

    public IdfEntryProxy(IFDEntry entry) {
        try {
            string = entry.toString();
            tag = entry.getEntryMeta().getTag();
            name = entry.getEntryMeta().getName();
            rawValue = entry.getRawValue();
        } catch (Exception ex) {
            Logger.getLogger(IdfEntryProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    public byte[] getRawValue() {
        return rawValue;
    }

    @Override
    public String toString() {
        return string;
    }

    public int getTag() {
        return tag;
    }

    @Override
    public int compareTo(IdfEntryProxy o) {
        return tag - o.tag;
    }
}
