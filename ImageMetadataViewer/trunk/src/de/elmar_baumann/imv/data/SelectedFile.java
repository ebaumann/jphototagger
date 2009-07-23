package de.elmar_baumann.imv.data;

import com.adobe.xmp.properties.XMPPropertyInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about the current selected file <em>only</em> when
 * <strong>one</strong> file is selected. Used for sharing data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-07
 */
public final class SelectedFile {

    public static final SelectedFile INSTANCE = new SelectedFile();
    private List<XMPPropertyInfo> xmpPropertyInfos;
    private File file = new File(""); // NOI18N

    /**
     * Sets the file and it's metadata.
     *
     * @param file                  file
     * @param xmpPropertyInfos      XMP metadata
     * @throws NullPointerException if the file is null
     */
    public synchronized void setFile(
            File file, List<XMPPropertyInfo> xmpPropertyInfos) {
        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        this.file = file;
        this.xmpPropertyInfos = xmpPropertyInfos == null
                                ? null
                                : new ArrayList<XMPPropertyInfo>(
                xmpPropertyInfos);
    }

    /**
     * Returns the file.
     *
     * @return File. If not set, a file with an empty name is returned.
     */
    public synchronized File getFile() {
        return file;
    }

    /**
     * Returns XMP metadata.
     *
     * @return XMP metadata or null if not set or set as null
     */
    public synchronized List<XMPPropertyInfo> getPropertyInfos() {
        return xmpPropertyInfos == null
               ? null
               : new ArrayList<XMPPropertyInfo>(xmpPropertyInfos);
    }

    private SelectedFile() {
    }
}
