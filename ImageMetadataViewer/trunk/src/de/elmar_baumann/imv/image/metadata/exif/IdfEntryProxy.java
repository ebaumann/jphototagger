package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifByteOrder;
import com.imagero.reader.tiff.IFDEntry;
import de.elmar_baumann.imv.app.AppLog;
import java.util.Arrays;

/**
 * Proxy for {@link com.imagero.reader.tiff.IFDEntry}. Reason: Files are
 * locked if used and could not be deleted and renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-15
 */
public final class IdfEntryProxy implements Comparable<IdfEntryProxy> {

    private int tag;
    private byte[] rawValue;
    private String string;
    private String name;
    private ExifByteOrder byteOrder;

    public IdfEntryProxy(IFDEntry entry) {
        try {
            string = entry.toString();
            tag = entry.getEntryMeta().getTag();
            name = entry.getEntryMeta().getName();
            rawValue = Arrays.copyOf(
                    entry.getRawValue(), entry.getRawValue().length);
            byteOrder = entry.parent.getByteOrder() == 0x4949 // 18761
                        ? ExifByteOrder.LITTLE_ENDIAN
                        : ExifByteOrder.BIG_ENDIAN;
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
    }

    public ExifByteOrder getByteOrder() {
        return byteOrder;
    }

    public String getName() {
        return name;
    }

    public byte[] getRawValue() {
        return Arrays.copyOf(rawValue, rawValue.length);
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
